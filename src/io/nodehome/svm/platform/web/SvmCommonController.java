package io.nodehome.svm.platform.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.biz.CoinListVO;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class SvmCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmCommonController.class);
	
	/*
	 * Terminal BN communication API
	 */
	@RequestMapping("/svm/common/querySendPacket")
	@ResponseBody
	public String querySendPacket(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
	    JSONParser jpTemp = new JSONParser();
	    JSONObject joReq;
	    System.out.println("******************************* querySendPacket start *******************************");
		
		// 시작 시간
		long sa_startTime = System.currentTimeMillis();
	    
		request.setCharacterEncoding("utf-8");
		
	    ByteArrayOutputStream osBuffer = new ByteArrayOutputStream();
	    byte[] bytTemp = new byte[2048];
	    int nReadBytes = 0;
	    ServletInputStream is =  request.getInputStream();
	    while((nReadBytes = is.read(bytTemp)) > 0) {
	    	osBuffer.write(bytTemp,0,nReadBytes);
	    }

	    byte[] bytQuery = osBuffer.toByteArray();
	    String strResponse = "";
	    String strContents = new String(bytQuery,"utf8");
		try {
		    //System.out.println("send_packet strContents : "+strContents.toString());
			joReq = (JSONObject)jpTemp.parse(strContents);
		    
			String netType = StringUtil.nvl(joReq.get("netType"),"testnet");
			String chainID = StringUtil.nvl(joReq.get("chainID"),"");
			System.out.println("querySendPacket chainID : "+chainID);
			
		    JSONObject joRes =  ApiHelper.postJSON(joReq, chainID);
		    
		    // 종료 시간
		    long sa_endTime = System.currentTimeMillis();
	        
	        // 경과시간
		    long sa_elapsedTime = sa_endTime - sa_startTime;
	        
	        // result
		    //System.out.println("sa_startTime : " + sa_startTime);
	        //System.out.println("sa_endTime : " + sa_endTime);
	        //System.out.println("sa_elapsedTime : " + sa_elapsedTime);
	        
		    joRes.put("sa_startTime", sa_startTime);
		    joRes.put("sa_endTime", sa_endTime);
		    joRes.put("sa_elapsedTime", sa_elapsedTime);
		    
		    //System.out.println("send_packet joRes : "+joRes.toString());
		    if(joRes != null)
		    	strResponse = joRes.toJSONString();
		    else
		    	strResponse = "{\"ec\":-1}";
		} catch (ParseException e) {
			e.printStackTrace();
		}

	    System.out.println("******************************* querySendPacket finish *******************************");
		System.out.println("querySendPacket strResponse : "+strResponse);
		return strResponse;
	}

	/*
	 * Nonce generation API for signing
	 * Nonce generation through block chain
	 */
	@RequestMapping("/svm/common/getNonce")
	@ResponseBody
	public String getNonce(@RequestBody HashMap<String,String> map, ModelMap model) throws UnknownHostException, IOException {
		String strPID = StringUtil.nvl(map.get("pid"));
		String strVER = StringUtil.nvl(map.get("ver"));
		String strCType = StringUtil.nvl(map.get("cType"));
		String npid= (String)map.get("npid");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		if(strCType==null || strCType.equals("")) strCType = ApiHelper.EC_CHAIN;
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		String strValue = "";
		String strNpid = "";


		// Start java call
		JSONObject joResult = null;
		if(strPID.equals("")) strPID = "PID";
		if(strVER.equals("")) strPID = "10000";

		if(npid==null || npid.equals(""))
			joResult = CPWalletUtil.Nonce(serviceID, strCType, strPID, strVER, chainID);
		else
			joResult = CPWalletUtil.Nonce(serviceID, strCType, strPID, strVER, npid, chainID);
		//System.out.println("Nonce - joResult : "+joResult.toString()); // Output to console
		
		long nCode = (Long)joResult.getOrDefault("ec",-1L);
		//System.out.println("nCode : "+nCode); // Output to console

		if(joResult!=null && joResult.get("value")!=null) {
			
			strValue = joResult.get("value").toString(); // On failure, the value is null, so an error occurs, so only on success
			strNpid = joResult.get("npid").toString(); // On failure, the value is null, so an error occurs, so only on success

			// strValue JSONParser
			JSONParser JpTemp = new JSONParser();
			JSONObject Row;
			try {
				Row = (JSONObject)JpTemp.parse(strValue);
				String strNonce = Row.get("nonce").toString();
				String ref = String.valueOf(Row.get("ref"));
				strValue = "{ \"result\": \"OK\", \"nonce\":\""+strNonce+"\", \"ref\":\""+ref+"\", \"npid\":\""+strNpid+"\" }";
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		} else {
			strValue = "{ \"result\":\"FAIL\" }";
		}

		//System.out.println("strValue : "+strValue);
		return strValue;
	}

	/*
	* Generate NONCE for coin trading
	* Description: The progress of the other api
	* step1 getNonce,
	* step2 getSignature,
	* step3 callAPI
	* creatTrans, commitData Both APIs
	* step1 createNonce for nonce creation,
	* step2 getSignature for nonce generation
	* step3 call / svm / common / getOwnerNonce
	* step4 get Signature
	* step5 call API
	* Go to Step 5.
	 */
	@RequestMapping("/svm/common/getNTransHistory")
	@ResponseBody
	public String getOwnerNonce(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"FAIL\" }";

		String npid= (String)map.get("npid");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValue(serviceID, ApiHelper.EC_CHAIN, "getNTransHistory", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			npid = (String)joResult.get("npid");

			if (nCode == 0) { // successful
				// values to pass to the web
				String strValue = joResult.get("value").toString();
				JSONParser jpTemp = new JSONParser();
				JSONObject joInfo;
				try {
					joInfo = (JSONObject)jpTemp.parse(strValue);
					if(joInfo!=null) {
						String total = String.valueOf(joInfo.get("total"));
						if(total==null || total.equals("") || total.equals("0")) {
							return "{ \"result\":\"FAIL\"}";
						}
						JSONArray rcd = (JSONArray)joInfo.get("Record");
						if(rcd!=null && rcd.size()>0) {
							JSONObject jvo = (JSONObject)rcd.get(0);
							strOK = "{ \"result\":\"OK\", \"nonce\":\""+jvo.get("sig")+"\", \"npid\":\""+npid+"\" }";
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} else { // fail
				strOK = "{ \"result\":\"FAIL\"}";
			}
		}

		System.out.println("getOwnerNonce strOK : "+strOK);

		return strOK;
	}
	
	/*
	 * get block chain coin information
	 */
//	@RequestMapping("/svm/common/getCoinInfo")
//	@ResponseBody
//	public String getCoinInfo(ModelMap model, @RequestBody HashMap<Object,Object> map) throws UnknownHostException, IOException {
//		String chainID = StringUtil.nvl(map.get("chainID"),"");
//		String strValue = "{}";
//		HashMap coin = CoinListVO.getCoinInfo(chainID);
//		if(coin!=null) {
//			strValue = "{\"nm\":\""+coin.get("nm")+"\",\"cou\":\""+coin.get("cou")+"\"}";
//		}
//		return strValue;
//	}

	/*
	 * Platform, javascript Cross-Origin Resource Sharing between SVMs API for communication without trouble
	 * Calling other domain json object request, response
	 * request : {"requestUrl" : "http://ds.nodehome.io/seedhost","serviceID":"nodehome","parameters":"~~~~~"}
	 * response : {"result":"OK", "list" : "values...."}
	 */
	@RequestMapping("/svm/common/callCorsUrl")
	@ResponseBody
	public String callCorsUrl(@RequestBody HashMap<String,Object> map) throws IOException {
		String requestUrl = "";
		if(map!=null) {
			requestUrl = (String)map.get("requestUrl");
			map.remove("requestUrl");
		}
		
	    String JSONInput = new JSONObject(map).toString();
	    if(JSONInput==null || JSONInput.equals("")) JSONInput = "{}";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
	    HttpEntity param= new HttpEntity(JSONInput, headers);

	    
	    HttpComponentsClientHttpRequestFactory factory  = new HttpComponentsClientHttpRequestFactory();
	    factory.setConnectTimeout(3*1000);
	    factory.setReadTimeout(3*1000);
	    RestTemplate restTemplate = new RestTemplate(factory);

	    
	    //RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.postForObject(requestUrl, param, String.class);

		return result;
	}

	@RequestMapping("/svm/common/getServiceInfo")
	@ResponseBody
	public String getServiceInfo(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		JSONObject joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "getServiceInfo", new String[] {"PID","10000",serviceID,"","",""} , chainID, netType);
		long nCode = (Long)joResult.getOrDefault("ec",-1L);
		if (nCode == 0) {
			String strValue = joResult.get("value").toString();

			strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"value\":"+strValue+" }";
		} else {
			strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"value\":{} }";
		}

		System.out.println("strOK : "+strOK);

		return strOK;
	}
	
	@RequestMapping("/svm/common/getApiVersion")
	@ResponseBody
	public String getApiVersion() throws UnknownHostException, IOException {
		String apiVersion = StringUtil.nvl(GlobalProperties.getProperty("Globals.apiVersion"));
		String strOK = "{ \"result\":\""+apiVersion+"\" }";
		return strOK;
	}
	
	@RequestMapping("/svm/common/getChainVersion")
	@ResponseBody
	public String getChainVersion(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strValue = "{ \"result\":\"FAIL\" }";

		String npid= (String)map.get("npid");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		JSONObject joResult = null;

		joResult = CPWalletUtil.getValue(serviceID, ApiHelper.EC_CHAIN, "version", npid, new String[]{"PID","10000"}, chainID );

		long nCode = (Long)joResult.getOrDefault("ec",-1L);
		npid = (String)joResult.get("npid");

		if (nCode == 0) { // successful
			// values to pass to the web
			strValue = joResult.get("value").toString();
		} else { // fail
			strValue = "{ \"result\":\"FAIL\"}";
		}

		return strValue;
	}

	@RequestMapping("/svm/common/getSvrVersion")
	@ResponseBody
	public String getSvrVersion() throws UnknownHostException, IOException {
		String svrVersion = StringUtil.nvl(GlobalProperties.getProperty("Globals.svrVersion"));
		String strOK = "{ \"result\":\""+svrVersion+"\" }";
		return strOK;
	}

	/*
	 * get coin info
	 */
	@RequestMapping("/svm/common/getCoinInfo")
	@ResponseBody
	public String getCoinInfo(@RequestBody HashMap<String,String> map) throws UnknownHostException, IOException {
		String strValue = "{ \"result\":\"FAIL\" }";
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"");
		HashMap vo = CoinListVO.getCoinInfo(chainID);
		return JSONValue.toJSONString(vo);
	}

	/*
	 * get policy info
	 */
	@RequestMapping("/svm/common/getPolicyInfo")
	@ResponseBody
	public String getPolicyInfo(@RequestBody HashMap<String,String> map) throws UnknownHostException, IOException {
		String strValue = "{ \"result\":\"FAIL\" }";
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"");
		HashMap vo = CoinListVO.getPolicyInfo(chainID);
		//System.out.println("strValue : "+strValue);
		return JSONValue.toJSONString(vo);
	}

}
