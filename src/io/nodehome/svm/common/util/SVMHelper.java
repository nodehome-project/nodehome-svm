package io.nodehome.svm.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.nodehome.cmm.service.GlobalProperties;

/**
 *  Description: A class that inquires system information
 */

public class SVMHelper {

	/*** Data registration in Blockchain
      *
      * @param
      *   String pcwalletPath: Install location of pc wallet in server
		, String svmNetworkHost: api service host url
		, String svmPid: Block Chain Communication PID
		, String svmVer: Block chain communication version
		, String addContentGroupId: Group ID to be registered for Contents Chain
		, String addKeyList: Contents key list
		, String addDataList: content data
		, String contentCommitMemo: note when registering
		, String privKey: Owner private key
		, String pubKey: Owner public key

     * @return String: success 0 / error code
     * @Example
    	String recordKey = DateUtil.getTimeStamp();
	    String ec = SVMHelper.addContent(
	    		 GlobalProperties.getProperty("pcwallet.path")
	    		,GlobalProperties.getProperty("svm.network.host")
	    		,"PID"
	    		,"10000"
	    		,"nodehome"
	    		,"[\\\"musiclist\\\",\\\""+recordKey+"\\\"]"
	    		,"{\\\"listId\\\":\\\""+nttId+"\\\",\\\"songTitle\\\":\\\""+board.getNttSj()+"\\\",\\\"atchId\\\":\\\""+board.getAtchFileId()+"\\\",\\\"atchSn\\\":\\\""+mp3FileSn+"\\\",\\\"artist\\\":\\\""+board.getNtcrNm()+"\\\",\\\"price\\\":\\\"1000\\\"}"
	    		,"Music List"
	    		,GlobalProperties.getProperty("pcwallet.privateKey")
	    		,GlobalProperties.getProperty("pcwallet.publicKey")
	    		);
     */
	public static String addContent(String pcwalletPath
			,String svmNetworkHost
			,String svmPid
			,String svmVer
			,String addContentGroupId
			,String addKeyList
			,String addDataList
			,String contentCommitMemo
			,String privKey
			,String pubKey
			) {

		String ec = "";
	    SigToolUtil sigToolUtil = new SigToolUtil(pcwalletPath);
	    JSONParser parser = new JSONParser();

	    String chainID = GlobalProperties.getProperty("project_chainID");
	    String netType = GlobalProperties.getProperty("project_net");
	    
	    // step 1 : get nonce
		String r1;
		try {
			r1 = SVMHelper.httpConnection(svmNetworkHost + "/svm/common/getNonce", "{\"pid\":\""+svmPid+"\", \"ver\":\""+svmVer+"\", \"nType\":\"query\", \"cType\":\"ecchain\", \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}");
			System.out.println("r1 : "+r1);
			
		    // ex : { "result": "OK", "nonce":"CwxkbgZ23x5", "npid":"03" }
		    JSONObject jsonResult = (JSONObject) parser.parse(r1);
		    if((jsonResult.get("result")).equals("OK")) {
		    	String sNonce = jsonResult.get("nonce").toString();
		    	String sNpid = jsonResult.get("npid").toString();

		    	// step 2 : get signature
		    	String inputStr = chainID + "queryreserveSetData"+svmPid+""+svmVer+""
		    			+ addContentGroupId
		    			+ addKeyList
		    			+ addDataList
		    			+ sNonce;
		    	System.out.println(inputStr+", "+privKey);
		    	String sSig = sigToolUtil.getGenerateSignature(inputStr, privKey, chainID);
		    	System.out.println("sSig : "+sSig);
		    	jsonResult = (JSONObject) parser.parse(sSig);
		    	sSig = jsonResult.get("Signature").toString();
		    	
		    	// step 3 : uploadData
		    	String sQuery = "{\"npid\":\""+sNpid+"\",\"serviceId\":\""+GlobalProperties.getProperty("project_serviceid")+"\","
		    			+ "\"parameterArgs\" : [\""+svmPid+"\",\""+svmVer+"\",\""+addContentGroupId+"\","
		    			+ "\""+addKeyList+"\","
		    			+ "\""+addDataList+"\","
		    			+ "\""+sNonce+"\","
		    			+ "\""+sSig+"\","
		    			+ "\""+pubKey+"\"], \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}";
		    	System.out.println("step3 sQuery : "+sQuery);
		    	System.out.println("svmNetworkHost : "+svmNetworkHost);
		    	String r2 = SVMHelper.httpConnection(svmNetworkHost + "/svm/content/reserveSetData", sQuery);
		    	System.out.println("step3 r2 : "+r2);
		    	jsonResult = (JSONObject) parser.parse(r2);
		    	String fee = jsonResult.get("fee").toString();
		    	String result = jsonResult.get("result").toString();
		    	if(result!=null && result.equals("OK")) {
			    	String reserve_id = jsonResult.get("reserve_id").toString();
			    	System.out.println("step3 : "+reserve_id);
			    	
			    	// step 4 : Get nonce for transaction history
			    	r1 = SVMHelper.httpConnection(svmNetworkHost + "/svm/common/getNonce", "{\"pid\":\""+svmPid+"\", \"npid\":\""+sNpid+"\",\"ver\":\""+svmVer+"\", \"nType\":\"query\", \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}");
			    	jsonResult = (JSONObject) parser.parse(r1);
			    	sNonce = jsonResult.get("nonce").toString();
			    	//sNpid = jsonResult.get("npid").toString();
			    	System.out.println("step4 : "+sNonce);

			    	// step 5 : Get signature for registration  / [<%=KeyManager.VERSION%>",fee,reserve_id,commitMemo,iNonce]
			    	sQuery = "invokepayFeeForReserve"+svmPid+""+svmVer+""+fee+reserve_id+contentCommitMemo+sNonce;
			    	sSig = sigToolUtil.getGenerateSignature(sQuery, privKey, chainID);
			    	jsonResult = (JSONObject) parser.parse(sSig);
			    	sSig = jsonResult.get("Signature").toString();
			    	System.out.println("step7 : "+sSig);
			    	
			    	// step 6 : Content registration
			    	sQuery = "{\"serviceId\":\""+GlobalProperties.getProperty("project_serviceid")+"\", \"npid\":\""+sNpid+"\","
			    			+ "\"parameterArgs\" : [\""+svmPid+"\",\""+svmVer+"\","
			    			+ "\""+fee+"\","
			    	    	+ "\""+reserve_id+"\","
			    	    	+ "\""+contentCommitMemo+"\","
			    	    	+ "\""+sNonce+"\","
			    			+ "\""+sSig+"\","
			    			+ "\""+pubKey+"\"], \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}";
			    	System.out.println("commitData sQuery : "+sQuery);
			    	r2 = SVMHelper.httpConnection(svmNetworkHost + "/svm/content/payFeeForReserve", sQuery);
			    	System.out.println("step8 : "+r2);
			    	jsonResult = (JSONObject) parser.parse(r2);
			    	ec = jsonResult.get("ec").toString();
			    	System.out.println("ec : "+ec);
		    	}
		    	
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ec;
	}

	/*** Delete Data in Blockchain
      *
      * @param
      *   String pcwalletPath: Install location of pc wallet in server
		, String svmNetworkHost: api service host url
		, String svmPid: Block Chain Communication PID
		, String svmVer: Block chain communication version
		, String addContentGroupId: Group ID to be registered for Contents Chain
		, String addKeyList: Contents key list
		, String privKey: Owner private key
		, String pubKey: Owner public key

     * @return String: success 0 / error code
     * @Example
    	String recordKey = DateUtil.getTimeStamp();
	    String ec = SVMHelper.addContent(
	    		 GlobalProperties.getProperty("pcwallet.path")
	    		,GlobalProperties.getProperty("svm.network.host")
	    		,"PID"
	    		,"10000"
	    		,"nodehome"
	    		,"[\\\"musiclist\\\",\\\""+recordKey+"\\\"]"
	    		,GlobalProperties.getProperty("pcwallet.privateKey")
	    		,GlobalProperties.getProperty("pcwallet.publicKey")
	    		);
     */
	public static String deleteData(String pcwalletPath
			,String svmNetworkHost
			,String svmPid
			,String svmVer
			,String addContentGroupId
			,String addKeyList
			,String privKey
			,String pubKey
			) {

		String ec = "";
	    SigToolUtil sigToolUtil = new SigToolUtil(pcwalletPath);
	    JSONParser parser = new JSONParser();

	    String chainID = GlobalProperties.getProperty("project_chainID");
	    String netType = GlobalProperties.getProperty("project_net");
	    
	    // step 1 : get nonce
		String r1;
		try {
			r1 = callCorsUrl(svmNetworkHost + "/svm/common/getNonce", "{\"pid\":\""+svmPid+"\", \"ver\":\""+svmVer+"\", \"nType\":\"invoke\", \"cType\":\"ecchain\", \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}");

		    // ex : { "result": "OK", "nonce":"CwxkbgZ23x5", "npid":"03" }
		    JSONObject jsonResult = (JSONObject) parser.parse(r1);
		    if((jsonResult.get("result")).equals("OK")) {
		    	String sNonce = jsonResult.get("nonce").toString();
		    	String sNpid = jsonResult.get("npid").toString();
		    	
		    	// step 2 : get signature
		    	String inputStr = chainID + "invokedeleteData"+svmPid+""+svmVer+""
		    			+ addContentGroupId
		    			+ addKeyList
		    			+ sNonce;
		    	String sSig = sigToolUtil.getGenerateSignature(inputStr, privKey, chainID);
		    	jsonResult = (JSONObject) parser.parse(sSig);
		    	sSig = jsonResult.get("Signature").toString();
		    	
		    	// step 3 : deleteData
		    	String sQuery = "{\"npid\":\""+sNpid+"\","
		    			+ "\"parameterArgs\" : [\""+svmPid+"\",\""+svmVer+"\",\""+addContentGroupId+"\","
		    			+ "\""+addKeyList+"\","
		    			+ "\""+sNonce+"\","
		    			+ "\""+sSig+"\","
		    			+ "\""+pubKey+"\"], \"chainID\":\""+chainID+"\", \"netType\":\""+netType+"\"}";
				System.out.println("sQuery : "+sQuery);
		    	String r2 = callCorsUrl(svmNetworkHost + "/svm/content/deleteData", sQuery);
		    	jsonResult = (JSONObject) parser.parse(r2);
		    	String result = jsonResult.get("result").toString();
		    	System.out.println("result : "+result);
		    	
		    	ec = result;
		    	
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return ec;
	}
	
	// Function / Asynchronous for Json communication with external server
	public static String callCorsUrl(String requestUrl, String map) throws IOException {
	    String JSONInput = map;
	    if(JSONInput==null || JSONInput.equals("")) JSONInput = "{}";
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	    HttpEntity param= new HttpEntity(JSONInput, headers);
	    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

	    String result = "";
	    try {
		    result = restTemplate.postForObject(requestUrl, param, String.class);
	    } catch (RestClientException e) {
	        //e.printStackTrace();
	    	;
	    }
	    headers.clear();
	    restTemplate=null;
		return result;
	}
	private static ClientHttpRequestFactory clientHttpRequestFactory() {
	    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	    factory.setReadTimeout(5000);
	    factory.setConnectTimeout(5000);
	    return factory;
	}   

	public static String httpConnection(String targetUrl, String param) {
		String returnText = "";
		// connect
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(targetUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			conn.setConnectTimeout(5000);

		} catch (MalformedURLException e2) {
			//e2.printStackTrace();
			return "";
		} catch (IOException e) {
			//e.printStackTrace();
			return "";
		}

		OutputStreamWriter osw = null;;
		try {
			osw = new OutputStreamWriter(conn.getOutputStream());
		} catch (IOException e1) {
			//e1.printStackTrace();
			return "";
		}
		try {
			osw.write(param);
			osw.flush();
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String line = null;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				returnText += line;
			}

			osw.close();
			br.close();
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			return "";
		} catch (ProtocolException e) {
			//e.printStackTrace();
			return "";
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			return "";
		} catch (IOException e) {
			//e.printStackTrace();
			return "";
		}
		return returnText;
	}

}
