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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	    JSONParser jpTemp = new JSONParser();
	    JSONObject joReq;
		try {
		    System.out.println("send_packet strContents : "+strContents.toString());
			joReq = (JSONObject)jpTemp.parse(strContents);
		    JSONObject joRes =  ApiHelper.postJSON(joReq);
		    System.out.println("send_packet joRes : "+joRes.toString());
		    if(joRes != null)
		    	strResponse = joRes.toJSONString();
		    else
		    	strResponse = "{\"ec\":-1}";
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    
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
		String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
		if(strCType==null || strCType.equals("")) strCType = ApiHelper.EC_CHAIN;

		String strValue = "";
		String strNpid = "";


		// Start java call
		JSONObject joResult = null;
		if(strPID.equals("")) strPID = "PID";
		if(strVER.equals("")) strPID = "10000";

		if(npid==null || npid.equals(""))
			joResult = CPWalletUtil.Nonce(serviceId, strCType, strPID, strVER);
		else
			joResult = CPWalletUtil.Nonce(serviceId, strCType, strPID, strVER, npid);
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
		String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");

		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValue(serviceId, ApiHelper.EC_CHAIN, "getNTransHistory", npid, arrArgs.toArray(new String[0]) );

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
	@RequestMapping("/svm/common/getCoinInfo")
	@ResponseBody
	public String getCoinInfo(ModelMap model) throws UnknownHostException, IOException {
		String strValue = "{}";
		HashMap coin = CoinListVO.getCoinInfo();
		if(coin!=null) {
			strValue = "{\"nm\":\""+coin.get("nm")+"\",\"cou\":\""+coin.get("cou")+"\"}";
		}
		return strValue;
	}

	/*
	 * Platform, javascript Cross-Origin Resource Sharing between SVMs API for communication without trouble
	 * Calling other domain json object request, response
	 * request : {"requestUrl" : "http://ds.nodehome.io/seedhost","serviceId":"nodehome","parameters":"~~~~~"}
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

	    RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.postForObject(requestUrl, param, String.class);

		return result;
	}
}
