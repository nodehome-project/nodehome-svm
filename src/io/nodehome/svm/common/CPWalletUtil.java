package io.nodehome.svm.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.util.KeyManager;

public class CPWalletUtil {
	
	// getNonce
	public static JSONObject Nonce(String serviceID, String chainCode, String strPID, String strVer, String chainID) {		
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = null;
		args = new String[] {strPID,strVer,"query"};

		joQuery.put("chaincode",chainCode);
		joQuery.put("serviceID",serviceID);
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");
		
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		return joRes;
	}

	public static JSONObject Nonce(String serviceID, String chainCode, String strPID, String strVer, String npid, String chainID) {		
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = null;
		args = new String[] {strPID,strVer,"query"};

		joQuery.put("chaincode",chainCode);
		joQuery.put("serviceID",serviceID);
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");
		joQuery.put("npid",npid);
		
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		return joRes;
	}

	/*
	 * Perform query.
	 * npid not specified
	 */
	public static JSONObject getValue(String serviceID, String chainCode, String functionName, String[] args, String chainID) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("serviceID",serviceID);
		joQuery.put("chaincode",chainCode);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
	
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		System.out.println("getValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}
	
	/*
	 * Perform query.
	 * Specify npid
	 */
	public static JSONObject getValue(String serviceID, String chainCode, String functionName, String npid, String[] args, String chainID) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("serviceID",serviceID);
		joQuery.put("chaincode",chainCode);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
	
		for( String strArg : args) {
			jaParam.add(strArg);
			//System.out.println(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		//System.out.println("getValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}

	public static JSONObject getValueManager(String serviceID, String chainCode, String functionName, String[] pArgs, String chainID, String netType) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = pArgs;

		String npid = ApiHelper.putQuerySignature(args,true,"query",chainCode, functionName, KeyManager.getPrivateKey(KeyManager.CPAPI_LEVEL.emMan300, netType, chainID), chainID);
		if((npid).equals("-1")) {
			return null;
		}
		joQuery.put("service_id",serviceID);
		joQuery.put("chaincode",chainCode);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
	
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		System.out.println("getValueManager - "+functionName+" : "+joRes.toString());
		
		return joRes;
	}
    
	// Invoke
	public static JSONObject putValue(String serviceID, String chainCode, String functionName, String npid, String[] args, String chainID) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("service_id",serviceID);
		joQuery.put("chaincode",chainCode);
		joQuery.put("query_type", "invoke");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
		
		for( String strArg : args) {
			jaParam.add(strArg);
			System.out.println("strArg : "+strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery, chainID);
		System.out.println("putValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}
	
}
