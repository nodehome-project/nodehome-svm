package io.nodehome.svm.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.libs.bitutil.crypto.Base58;
import io.nodehome.libs.bitutil.crypto.HdKeyNode;
import io.nodehome.libs.bitutil.crypto.InMemoryPrivateKey;
import io.nodehome.libs.bitutil.crypto.Signature;
import io.nodehome.libs.bitutil.model.NetConfig;
import io.nodehome.libs.bitutil.model.NetConfig.NetworkType;
import io.nodehome.libs.bitutil.util.HashUtils;
import io.nodehome.libs.bitutil.util.Sha256Hash;

public class KeyManager {

	/*
	 * //main net private static final String[] _sPubKeys = new String[] {
	 * "KwKCtJSaSoF3R67GRGFZSv12fBcxg912ktyRH9UEY92VrSGDW9AF",
	 * "KwJEWNUP1vr9SYHfYH1bnhr6kXh6bYxT5CCETVSuUN6LVhZdrtHW",
	 * "KwLfJWVwvkKw8ZpDKR8ddWjqReHVEymwiDWnwaY2SoQhaPRrsEvQ",
	 * "KwLmBoJwPDg2XaLV6HJpk25TUXZZJSQ5fp9g5ZhgzL8eg21VTmxx", }; // test net
	 * private static final String[] _sPubKeys = new String[] {
	 * "cMgCMDSRsrwJaXaXog4gpEW6HQvNLb6ipw7tPZvk3FgW7BFae9nm",
	 * "cMfDyHUESzYQbykvvgpjA2MANkzWG1499ELhZuuQyUkLkSanN1kt",
	 * "cMhemRVoMp2CJ1HUhpwkzqEu3satuRsdnFfG3zzXwv4hq8Wro1Q3",
	 * "cMhkeiJnpHNHh1okUh7x7LaX6krxxtVmjrJ9BzACVSnevm5WHtRP", }; private static
	 * final String[] _sSeeds = new String[] {
	 * "9ba8a4c5d395381a03b2e8f619addbe5", "deaaf34820b1d777652a55b0bdaecfa2",
	 * "0b5b1baecd180e975321fe10c1a14370", "561ae87e4b3e44afa44b77e8871763de",
	 * };
	 */

	public enum CPAPI_LEVEL {
		emRoot, emMan100, emMan200, emMan300,
	};

	//private static final InMemoryPrivateKey[] sm_memKeys = { null, null, null, null, null };
	private static final HashMap keyList = new HashMap();
	private static final String KEY_FILE_PATH = (GlobalProperties.class.getResource("").getPath().substring(0, GlobalProperties.class.getResource("").getPath().lastIndexOf("io"))) + "resources/props" + System.getProperty("file.separator");

	public static String PID = "PID";
	public static String VERSION = "10000";
	
	public static InMemoryPrivateKey getPrivateKey(CPAPI_LEVEL emLV, String netType, String chainID) {
		HdKeyNode hdKN = null;
		int nKeyIdx = 0;
		switch (emLV) {
		case emRoot:
			nKeyIdx = 0;
			break;
		case emMan100:
			nKeyIdx = 1;
			break;
		case emMan200:
			nKeyIdx = 2;
			break;
		case emMan300:
			nKeyIdx = 3;
			break;
		default:
			return null;
		}
		netType = netType.toLowerCase();
		
		InMemoryPrivateKey[] sm_memKeys = (InMemoryPrivateKey[])keyList.get(netType+chainID);
		if(sm_memKeys==null || (sm_memKeys.length-1)<nKeyIdx || sm_memKeys[nKeyIdx]==null) sm_memKeys = reloadManagerKey(netType, chainID);
		return sm_memKeys[nKeyIdx];
	}

	public static InMemoryPrivateKey[] reloadManagerKey(String netType, String chainID) {
		netType = netType.toLowerCase();

		InMemoryPrivateKey[] sm_memKeys = { null, null, null, null, null };
		if(netType.equals("test")) NetConfig.setDefaultNet(NetworkType.TEST);
		if(netType.equals("biz")) NetConfig.setDefaultNet(NetworkType.BIZ);
		if(netType.equals("dev")) NetConfig.setDefaultNet(NetworkType.DEV);
		sm_memKeys[3] = loadPrivateKey(NetConfig.defaultNet, KEY_FILE_PATH + "man300-"+chainID+"-"+netType+".key");
		keyList.put(netType+chainID, sm_memKeys);
		
		return sm_memKeys;
	}

	public static String getPubKey(byte[] input) {
		byte[] b = new byte[input.length + 5];
		b[0] = NetConfig.defaultNet.getNetID();
		System.arraycopy(input, 0, b, 1, input.length);
		Sha256Hash checkSum = HashUtils.doubleSha256(b, 0, input.length + 1);
		System.arraycopy(checkSum.getBytes(), 0, b, input.length + 1, 4);
		return Base58.encode(b);
	}

	private static final String sm_strKeyFileHeader = "// NODEHOME KEY FILE";

	public static InMemoryPrivateKey loadPrivateKey(NetConfig network, byte[] bytKey) {
		BufferedReader rdKey = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytKey)));
		return loadPrivateKey(network, rdKey);
	}

	public static InMemoryPrivateKey loadPrivateKey(NetConfig network, String strFilename) {
		BufferedReader rdFile;
		InMemoryPrivateKey memPriK = null;
		try {
			rdFile = new BufferedReader(new FileReader(strFilename));
			memPriK = loadPrivateKey(network, rdFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memPriK;
	}

	private static InMemoryPrivateKey loadPrivateKey(NetConfig network, BufferedReader reader) {
		String strKeyB58 = "";
		String strLine = "";
		String strName = "";
		String strValue = "";
		// String strVer = "1.0";
		InMemoryPrivateKey priKey = null;
		try {
			int nLineCount = 0;
			boolean bFindHeader = false;
			int nDelimPos = 0;

			while ((strLine = reader.readLine()) != null && nLineCount < 100) {
				nLineCount++;
				strLine = strLine.trim();
				if (strLine.equals(sm_strKeyFileHeader)) {
					bFindHeader = true;
					continue;
				}
				if (!bFindHeader)
					continue;
				if (strLine.startsWith("//"))
					continue;
				nDelimPos = strLine.indexOf(':');
				if (nDelimPos <= 0) {
					continue;
				}
				strName = strLine.substring(0, nDelimPos).trim();
				strValue = strLine.substring(nDelimPos + 1).trim();
				// if(strName.equals("ver"))
				// strVer = strValue;
				if (strName.equals("key"))
					strKeyB58 = strValue;
			}
			reader.close();
			if (!strKeyB58.isEmpty()) {
				priKey = InMemoryPrivateKey.fromBase58String(strKeyB58, network);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return priKey;
	}

	public static String sigPacket(String strContents, InMemoryPrivateKey priK) {
		// InMemoryPrivateKey memK = new
		// InMemoryPrivateKey(priK.getPrivateKeyBytes(),priK.getPublicKey().isCompressed());
		Sha256Hash toSign = HashUtils.sha256(strContents.getBytes());
		Signature strSig = priK.generateSignature(toSign);
		return Base58.encode(strSig.derEncode());
	}

}
