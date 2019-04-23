package io.nodehome.svm.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.util.DateUtil;
import io.nodehome.svm.common.util.KeyManager;

public class CPWalletUtil {
	
	// getNonce
	public static JSONObject Nonce(String serviceId, String chainType, String strPID, String strVer) {		
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = null;
		args = new String[] {strPID,strVer,"query"};

		joQuery.put("chaincode",chainType);
		joQuery.put("serviceId",serviceId);
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");
		
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery);
		return joRes;
	}

	public static JSONObject Nonce(String serviceId, String chainType, String strPID, String strVer, String npid) {		
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = null;
		args = new String[] {strPID,strVer,"query"};

		joQuery.put("chaincode",chainType);
		joQuery.put("serviceId",serviceId);
		joQuery.put("query_type","query");
		joQuery.put("func_name","getNonce");
		joQuery.put("npid",npid);
		
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery);
		return joRes;
	}

	/*
	 * Perform query.
	 * npid not specified
	 */
	public static JSONObject getValue(String serviceId, String chainType, String functionName, String[] args) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("serviceId",serviceId);
		joQuery.put("chaincode",chainType);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
	
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery);
		System.out.println("getValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}
	
	/*
	 * Perform query.
	 * Specify npid
	 */
	public static JSONObject getValue(String serviceId, String chainType, String functionName, String npid, String[] args) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("serviceId",serviceId);
		joQuery.put("chaincode",chainType);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
	
		for( String strArg : args) {
			jaParam.add(strArg);
			System.out.println(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery);
		System.out.println("getValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}

	public static JSONObject getValueManager(String serviceId, String chainType, String functionName, String[] pArgs) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();
		
		String[] args = pArgs;

		String npid = ApiHelper.putQuerySignature(args,true,"query",chainType, functionName, KeyManager.getPrivateKey(KeyManager.CPAPI_LEVEL.emMan300));
		if((npid).equals("-1")) {
			return null;
		}
		joQuery.put("service_id",serviceId);
		joQuery.put("chaincode",chainType);
		joQuery.put("query_type","query");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
	
		for( String strArg : args) {
			jaParam.add(strArg);
		}
		joQuery.put("func_args", jaParam);  
	
		JSONObject joRes = ApiHelper.postJSON(joQuery);
		System.out.println("getValueManager - "+functionName+" : "+joRes.toString());
		
		return joRes;
	}
    
	// Invoke
	public static JSONObject putValue(String serviceId, String chainType, String functionName, String npid, String[] args) {
		JSONObject joQuery = new JSONObject();
		JSONArray jaParam = new JSONArray();

		joQuery.put("service_id",serviceId);
		joQuery.put("chaincode",chainType);
		joQuery.put("query_type", "invoke");
		joQuery.put("func_name",functionName);
		joQuery.put("npid",npid);
		
		for( String strArg : args) {
			jaParam.add(strArg);
			System.out.println("strArg : "+strArg);
		}
		joQuery.put("func_args", jaParam);  

		JSONObject joRes = ApiHelper.postJSON(joQuery);
		System.out.println("putValue - "+functionName+" : "+joRes.toString());
		return joRes;
	}
	
}
