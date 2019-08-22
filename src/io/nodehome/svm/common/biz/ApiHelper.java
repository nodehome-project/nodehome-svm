package io.nodehome.svm.common.biz;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.libs.bitutil.crypto.Base58;
import io.nodehome.libs.bitutil.crypto.InMemoryPrivateKey;
import io.nodehome.libs.bitutil.crypto.PublicKeyHelper;
import io.nodehome.libs.bitutil.crypto.Signature;
import io.nodehome.libs.bitutil.model.NetConfig;
import io.nodehome.libs.bitutil.util.HashUtils;
import io.nodehome.libs.bitutil.util.Sha256Hash;
import io.nodehome.svm.common.util.EtcUtils;
import io.nodehome.svm.common.util.LogUtil;
import io.nodehome.svm.common.util.security.SimpleRandomSource;

public class ApiHelper {

	public static final String EC_CHAIN = "ecchain";
	public static List apiHosts = new ArrayList();
	
	private static Logger sm_Log = Logger.getLogger(ApiHelper.class.getName());

	public static String putQuerySignature(String[] args, boolean bNeedReqNonce, String queryType, String chainCode, String functionName, InMemoryPrivateKey priKey, String chainID) {
		return putQuerySignature("", args, bNeedReqNonce, queryType, chainCode, functionName, priKey, chainID);
	}
	public static String putQuerySignature(String host, String[] args, boolean bNeedReqNonce, String queryType, String chainCode, String functionName, InMemoryPrivateKey priKey, String chainID) {

		if(priKey == null)
			return "-1";
		
		String[] strNonce = new String[] {"",""};
		if(bNeedReqNonce) {
			//strNonce = getNonce(strFuncType);
			strNonce = getNonce(host, queryType, chainCode, chainID);
			//if(strNonce.isEmpty())
			if(strNonce==null || strNonce[0]==null || strNonce[0].equals(""))
				return "-1";
		} else {
			strNonce[0] = Base58.encode(sm_rndSource.nextBytes()).substring(0, 10);
		}
		StringBuffer strbContent = new StringBuffer();
		args[args.length-3] = strNonce[0];
		for(int i = 0; i < args.length-2 ; i ++) {
			strbContent.append(args[i]);
		}

		Sha256Hash toSign = HashUtils.sha256((  chainID+queryType+functionName+strbContent.toString()  ).getBytes());
		Signature sig = priKey.generateSignature(toSign);
		String strSig = Base58.encode(sig.derEncode());
		String strPubK = PublicKeyHelper.getBase58EncodedPublicKey(priKey.getPublicKey(), NetConfig.defaultNet);
		args[args.length-2] = strSig;
		args[args.length-1] = strPubK;
		
		return strNonce[1];
	}

	public static String[] getNonce(String host, String strFuncType, String chainCode, String chainID) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		joQuery.put("serviceID",GlobalProperties.getProperty("project_serviceID"));
		joQuery.put("chaincode",chainCode);
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");
		
		jaParam.add("PID");
		jaParam.add("10000");
		jaParam.add(strFuncType);
		joQuery.put("func_args", jaParam);
		
		String strNonce = "";
		String strNpid = "";
		JSONObject joRes = postJSON(host, joQuery, chainID);
		if(joRes != null) {
			long nCode = (Long)joRes.getOrDefault("ec",-1);
			if(nCode == 0) {
				strNpid = joRes.get("npid").toString();
				String strValue = (joRes.getOrDefault("value","{}")).toString();
				JSONParser jaTemp = new JSONParser();
				JSONObject joNonce = null;
				try {
					joNonce = (JSONObject)jaTemp.parse(strValue);
				} 
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(joNonce != null)
					strNonce = (String)joNonce.getOrDefault("nonce","");
			}
		}
		return new String[] {strNonce,strNpid};
	}
	
	public static String getAPIHost(String chainID) {
        int num = 0;
        apiHosts = NAHostVO.getNAHosts(chainID);
        
    	if(apiHosts==null || apiHosts.size()>0) {
        	Random generator = new Random();
    		num = generator.nextInt(apiHosts.size());
    		return (String)apiHosts.get(num);
    	}
    	return "";
	}

	public static JSONObject postJSON(JSONObject joQuery, String chainID) {
		String strUrl = getAPIHost(chainID);
		if(!strUrl.equals("")) {
			return postJSON(strUrl, joQuery, chainID);
		}
		return new JSONObject();
	}

	public static JSONObject postJSON(String host, JSONObject joQuery, String chainID) {
		//String strUrl = getAPIHost() + GlobalProperties.getProjectApiHost();
		if(host == null || host.equals("")) host = getAPIHost(chainID);
		
		String strUrl = host + GlobalProperties.getProjectApiHost();
		JSONObject joResponse = null;
		byte[] bytResponse = null;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(strUrl);
		method.setRequestHeader("Content-type", "application/json");
		String strQuery = joQuery.toString();
		method.setRequestEntity( new StringRequestEntity( strQuery ) );
		try {
			LogUtil.d(sm_Log,strQuery);
			int statusCode = client.executeMethod(method);
					
			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = method.getResponseBodyAsStream();
				bytResponse = EtcUtils.readFile(is);
			} else {
				System.out.println("fail : " + statusCode + " : url : " +  strUrl );
			}
		} catch (HttpException e1) {
			//e1.printStackTrace();
			String strResponse = "{ \"ec\":-1,\"value\":\"{}\",\"ref\":\"Connect fail [HttpException]\" }";
			JSONParser paRes = new JSONParser();
			try {
				joResponse = (JSONObject) paRes.parse(strResponse);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return joResponse;
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
			String strResponse = "{ \"ec\":-1,\"value\":\"{}\",\"ref\":\"Connect fail [HttpException]\" }";
			JSONParser paRes = new JSONParser();
			try {
				joResponse = (JSONObject) paRes.parse(strResponse);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return joResponse;
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		
//		String strReturn = "";

		if(bytResponse != null) {
			try {
				String strResponse = new String(bytResponse,"utf-8").trim();
				if(!strResponse.startsWith("{") ) {  
					strResponse.replace('\"', '\'');
					strResponse = "{ \"ec\":-1,\"value\":\"{}\",\"ref\":\"" + strResponse + "\" }";
				}
				JSONParser paRes = new JSONParser();
				joResponse = (JSONObject) paRes.parse(strResponse);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return joResponse;
	}

	public static String postJSON(String requestUrl, String map) throws IOException {
		JSONObject joResponse = null;
		byte[] bytResponse = null;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(requestUrl);
		method.setRequestHeader("Content-type", "application/json");
		String strQuery = map;
				
		method.setRequestEntity( new StringRequestEntity( strQuery ) );
		try {
			int statusCode = client.executeMethod(method);
					
			if (statusCode == HttpStatus.SC_OK) {
				InputStream is = method.getResponseBodyAsStream();
				bytResponse = EtcUtils.readFile(is);
			} else {
				/*InputStream is = method.getResponseBodyAsStream();
				bytResponse = EtcUtils.readFile(is);
				System.out.println("bytResponse : " +  bytResponse );
				System.out.println("fail : " + statusCode + " : url : " +  requestUrl );*/
				return "";
			}
		} catch (HttpException e1) {
			//e1.printStackTrace();
			return "";
		} catch (IOException e2) {
			//e2.printStackTrace();
			return "";
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		
		if(bytResponse != null) {
			try {
				String strResponse = new String(bytResponse,"utf-8").trim();
				System.out.println("strResponse : " +  strResponse );
				if(!strResponse.startsWith("{") ) {  
					strResponse.replace('\"', '\'');
					strResponse = "{ \"ec\":-1,\"value\":\"{}\",\"ref\":\"" + strResponse + "\" }";
				}
				JSONParser paRes = new JSONParser();
				joResponse = (JSONObject) paRes.parse(strResponse);
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
				return "";
			} catch (ParseException e) {
				//e.printStackTrace();
				return "";
			}
		}
		return joResponse.toJSONString();
	}
	
	private static boolean putSignature(String[] args, boolean bNeedReqNonce,String queryType, String functionName,InMemoryPrivateKey priKey, String chainID) {
		if(priKey == null)
			return false;
		
		String strNonce = "";
		if(bNeedReqNonce) {
			strNonce = getNonce2(queryType, chainID);
			if(strNonce.isEmpty())
				return false;
		} else {
			strNonce = Base58.encode(sm_rndSource.nextBytes()).substring(0, 10);
		}
		StringBuffer strbContent = new StringBuffer();
		args[args.length-3] = strNonce;
		for(int i = 0; i < args.length-2 ; i ++)
		{
			strbContent.append(args[i]);
		}
	
		Sha256Hash toSign = HashUtils.sha256((chainID+queryType+functionName+strbContent.toString()).getBytes());
		Signature sig = priKey.generateSignature(toSign);
		String strSig = Base58.encode(sig.derEncode());
		String strPubK = PublicKeyHelper.getBase58EncodedPublicKey(priKey.getPublicKey(), NetConfig.defaultNet);
		args[args.length-2] = strSig;
		args[args.length-1] = strPubK;
		
		return true;
	}

	public static String getNonce2(String strFuncType, String chainID) {
	   // {"query_type":"query","func_name":"getNonce","func_args":["PID","10000","query"]}
		assert(strFuncType.equals("query") || strFuncType.equals("invoke"));
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");

		jaParam.add("PID");
		jaParam.add("10000");
		jaParam.add(strFuncType);
		joQuery.put("func_args", jaParam);
		
		String strNonce = "";
		JSONObject joRes = postJSON(joQuery, chainID);
		if(joRes != null)
		{
			long nCode = (Long)joRes.getOrDefault("ec",-1);
			if(nCode == 0)
			{
				String strValue = (joRes.getOrDefault("value","{}")).toString();
				JSONParser jaTemp = new JSONParser();
				JSONObject joNonce = null;
				try
				{
					joNonce = (JSONObject)jaTemp.parse(strValue);
				} 
				catch (ParseException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(joNonce != null)
					strNonce = (String)joNonce.getOrDefault("nonce","");
			}
		}
		// CPWorkbench.logMessage("getNonce:" + strNonce);
		return strNonce;
	}
	
	private static SimpleRandomSource sm_rndSource = new SimpleRandomSource();
	public static boolean putInvokeSignature(String[] args, boolean bNeedReqNonce,String functionName,InMemoryPrivateKey priKey, String chainID)
	{
		return putSignature(args, bNeedReqNonce, "invoke", functionName, priKey, chainID);	
	}

	// nonce, privKey direct typing
	public static boolean putSignatureWithNonce(String[] args, String strNonce,String queryType,String functionName, InMemoryPrivateKey priKey, String chainID) {
	
		if(priKey == null)
			return false;
		
		StringBuffer strbContent = new StringBuffer();
		args[args.length-3] = strNonce;
		for(int i = 0; i < args.length-2 ; i ++)
		{
			strbContent.append(args[i]);
		}
	
		Sha256Hash toSign = HashUtils.sha256((chainID+queryType+functionName+strbContent.toString()).getBytes());
		Signature sig = priKey.generateSignature(toSign);
		String strSig = Base58.encode(sig.derEncode());
		String strPubK = PublicKeyHelper.getBase58EncodedPublicKey(priKey.getPublicKey(), NetConfig.defaultNet);
		args[args.length-2] = strSig;
		args[args.length-1] = strPubK;
		
		return true;
	}
}
