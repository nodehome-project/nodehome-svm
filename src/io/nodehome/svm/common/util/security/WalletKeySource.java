package io.nodehome.svm.common.util.security;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.util.KeyManager;
import net.fouri.libs.bitutil.crypto.Base58;
import net.fouri.libs.bitutil.crypto.InMemoryPrivateKey;
import net.fouri.libs.bitutil.crypto.PublicKeyHelper;
import net.fouri.libs.bitutil.model.NetConfig;

public class WalletKeySource {
	
	public static HashMap getKey () {
		HashMap map = new HashMap();
		
		SimpleRandomSource rndS = new SimpleRandomSource();

		HdKeyNode sm_hdNodeRootLast = HdKeyNode.fromSeed(rndS.nextBytes());
        
		String returnKey = ""; 
		
        if(sm_hdNodeRootLast != null) {
        	InMemoryPrivateKey memPrivKey = sm_hdNodeRootLast.getPrivateKey();

            String strPubKeyB58 = PublicKeyHelper.getBase58EncodedPublicKey(memPrivKey.getPublicKey(), NetConfig.defaultNet);
            //System.out.println("publicKey : " + strPubKeyB58);            

            String strPrivKey = memPrivKey.getBase58EncodedPrivateKey(NetConfig.defaultNet);
            //System.out.println("privateKey : " + strPrivKey);
            
			map.put("publicKey",strPubKeyB58);
			map.put("privateKey",strPrivKey);
        }
        return map;
	}
	
	public static String encodeB58Pubkey(byte[] pubK,byte netID) {
        byte[] b = new byte[pubK.length + 1];
        b[0] = netID;
        System.arraycopy(pubK, 0, b, 1, pubK.length);
        return Base58.encodeWithChecksum(b);
    }

}
