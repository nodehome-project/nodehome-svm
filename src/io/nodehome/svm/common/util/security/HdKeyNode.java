package io.nodehome.svm.common.util.security;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.UUID;

import com.google.common.base.Preconditions;

import net.fouri.libs.bitutil.crypto.Hmac;
import net.fouri.libs.bitutil.crypto.InMemoryPrivateKey;
import net.fouri.libs.bitutil.crypto.PublicKey;
import net.fouri.libs.bitutil.crypto.ec.Parameters;
import net.fouri.libs.bitutil.util.BitUtils;
import net.fouri.libs.bitutil.util.ByteReader;
import net.fouri.libs.bitutil.util.ByteWriter;

public class HdKeyNode
  implements Serializable
{
  public static final int HARDENED_MARKER = -2147483648;
  private static final int CHAIN_CODE_SIZE = 32;
  private final InMemoryPrivateKey _privateKey;
  private final PublicKey _publicKey;
  private final byte[] _chainCode;
  private final int _depth;
  private final int _parentFingerprint;
  private final int _index;
  private final byte[] _seed;
  private static final byte[] PRODNET_PUBLIC = { 4, -120, -78, 30 };
  private static final byte[] TESTNET_PUBLIC = { 4, 53, -121, -49 };
  private static final byte[] PRODNET_PRIVATE = { 4, -120, -83, -28 };
  private static final byte[] TESTNET_PRIVATE = { 4, 53, -125, -108 };

  public void toCustomByteFormat(ByteWriter writer)
  {
    byte flag = 0;
    if (isPrivateHdKeyNode()) {
      flag = 1;
    }

    if (this._seed != null) {
      if (this._seed.length == 16)
        flag = (byte)(flag | 0x2);
      else if (this._seed.length == 32)
        flag = (byte)(flag | 0x4);
      else if (this._seed.length == 64)
        flag = (byte)(flag | 0x6);
    }
    if (isPrivateHdKeyNode()) {
      flag = (byte)(flag | 0x1);
    }

    writer.put(flag);

    if ((flag & 0x1) == 1) {
      Preconditions.checkArgument(this._privateKey.getPrivateKeyBytes().length == 32);
      writer.putBytes(this._privateKey.getPrivateKeyBytes());
    }

    Preconditions.checkArgument(this._publicKey.getPublicKeyBytes().length == 33);
    writer.putBytes(this._publicKey.getPublicKeyBytes());
    writer.putBytes(this._chainCode);
    writer.putIntLE(this._depth);
    writer.putIntLE(this._parentFingerprint);
    writer.putIntLE(this._index);
    if ((flag & 0x6) != 0)
      writer.putBytes(this._seed);
  }

  public byte[] toCustomByteFormat()
  {
    ByteWriter writer = new ByteWriter(1024);
    toCustomByteFormat(writer);
    return writer.toBytes();
  }

  public static HdKeyNode fromCustomByteformat(byte[] bytes)
    throws ByteReader.InsufficientBytesException
  {
    return fromCustomByteformat(new ByteReader(bytes));
  }

  public static HdKeyNode fromCustomByteformat(ByteReader reader)
    throws ByteReader.InsufficientBytesException
  {
    byte flag = reader.get();
    boolean hasPrivateKey = (flag & 0x1) == 1;
    int nSeedBytes = 0;

    if ((flag & 0x6) == 2) nSeedBytes = 16;
    else if ((flag & 0x6) == 4) nSeedBytes = 32;
    else if ((flag & 0x6) == 6) nSeedBytes = 64;

    if (hasPrivateKey)
    {
      InMemoryPrivateKey privateKey = new InMemoryPrivateKey(reader.getBytes(32), reader.getBytes(33));
      return new HdKeyNode(privateKey, reader.getBytes(32), reader.getIntLE(), reader.getIntLE(), 
        reader.getIntLE(), reader.getBytes(nSeedBytes));
    }

    return new HdKeyNode(new PublicKey(reader.getBytes(33)), reader.getBytes(32), reader.getIntLE(), 
      reader.getIntLE(), reader.getIntLE());
  }

  private HdKeyNode(InMemoryPrivateKey privateKey, byte[] chainCode, int depth, int parentFingerprint, int index, byte[] seed)
  {
    this._privateKey = privateKey;
    this._publicKey = this._privateKey.getPublicKey();
    this._chainCode = chainCode;
    this._depth = depth;
    this._parentFingerprint = parentFingerprint;
    this._index = index;
    if (seed == null)
      this._seed = new byte[0];
    else
      this._seed = seed;
  }

  public HdKeyNode(PublicKey publicKey, byte[] chainCode, int depth, int parentFingerprint, int index)
  {
    this._privateKey = null;
    this._publicKey = publicKey;
    this._chainCode = chainCode;
    this._depth = depth;
    this._parentFingerprint = parentFingerprint;
    this._index = index;
    this._seed = new byte[0];
  }

  public byte[] getSeed()
  {
    return this._seed;
  }

  public static HdKeyNode fromSeed(byte[] seed) throws HdKeyNode.KeyGenerationException {
    Preconditions.checkArgument((seed.length == 16) || (seed.length == 32) || (seed.length == 64), "seed must be 128,256,512 bits");
    byte[] I = Hmac.hmacSha512(asciiStringToBytes("Nodehome seed"), seed);
    byte[] IL = BitUtils.copyOfRange(I, 0, 32);
    BigInteger k = new BigInteger(1, IL);
    if (k.compareTo(Parameters.n) >= 0) {
      throw new KeyGenerationException(
        "An unlikely thing happened: The derived key is larger than the N modulus of the curve");
    }
    if (k.equals(BigInteger.ZERO)) {
      throw new KeyGenerationException("An unlikely thing happened: The derived key is zero");
    }
    InMemoryPrivateKey privateKey = new InMemoryPrivateKey(IL, true);

    byte[] IR = BitUtils.copyOfRange(I, 32, 64);
    return new HdKeyNode(privateKey, IR, 0, 0, 0, seed);
  }

  public boolean isPrivateHdKeyNode()
  {
    return this._privateKey != null;
  }

  public HdKeyNode getPublicNode()
  {
    return new HdKeyNode(this._publicKey, this._chainCode, this._depth, this._parentFingerprint, this._index);
  }

  private byte[] bigIntegerTo32Bytes(BigInteger b)
  {
    byte[] bytes = b.toByteArray();
    Preconditions.checkArgument(bytes.length <= 33);
    if (bytes.length == 33)
    {
      Preconditions.checkArgument(bytes[0] == 0);
      return BitUtils.copyOfRange(bytes, 1, 33);
    }

    byte[] result = new byte[32];
    System.arraycopy(bytes, 0, result, result.length - bytes.length, bytes.length);
    return result;
  }

  public int getFingerprint()
  {
    byte[] hash = this._publicKey.getPublicKeyHash();
    int fingerprint = (hash[0] & 0xFF) << 24;
    fingerprint += ((hash[1] & 0xFF) << 16);
    fingerprint += ((hash[2] & 0xFF) << 8);
    fingerprint += (hash[3] & 0xFF);
    return fingerprint;
  }

  public InMemoryPrivateKey getPrivateKey()
    throws HdKeyNode.KeyGenerationException
  {
    if (!isPrivateHdKeyNode()) {
      throw new KeyGenerationException("Not a private HD key node");
    }
    return this._privateKey;
  }

  public PublicKey getPublicKey()
  {
    return this._publicKey;
  }


  public String toString()
  {
    return "Fingerprint: " + Integer.toString(getFingerprint());
  }

  private static byte[] asciiStringToBytes(String string) {
    try {
      return string.getBytes("US-ASCII"); } catch (UnsupportedEncodingException e) {
    }
    throw new RuntimeException();
  }

  public int hashCode()
  {
    return this._publicKey.hashCode();
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof HdKeyNode)) {
      return false;
    }
    HdKeyNode other = (HdKeyNode)obj;
    if (!this._publicKey.equals(other._publicKey)) {
      return false;
    }
    if (this._depth != other._depth) {
      return false;
    }
    if (this._parentFingerprint != other._parentFingerprint) {
      return false;
    }
    if (this._index != other._index) {
      return false;
    }
    if (!BitUtils.areEqual(this._chainCode, other._chainCode)) {
      return false;
    }
    return isPrivateHdKeyNode() == other.isPrivateHdKeyNode();
  }

  public int getIndex()
  {
    return this._index;
  }

  public int getParentFingerprint()
  {
    return this._parentFingerprint;
  }

  public int getDepth()
  {
    return this._depth;
  }

  public UUID getUuid()
  {
    byte[] publicKeyBytes = getPublicKey().getPublicKeyBytes();
    return new UUID(BitUtils.uint64ToLong(publicKeyBytes, 8), BitUtils.uint64ToLong(
      publicKeyBytes, 16));
  }

  public static class KeyGenerationException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public KeyGenerationException(String message)
    {
      super();
    }
  }
}