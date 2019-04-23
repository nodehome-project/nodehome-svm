package io.nodehome.svm.platform.web;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.util.KeyManager;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class SvmServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmServiceController.class);

	 /*
	  * add service
	  */
	@RequestMapping("/svm/service/registerService")
	@ResponseBody
	public String reserveRegisterService(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		String npid= (String)map.get("npid");
		String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.putValue(serviceId, ApiHelper.EC_CHAIN, "registerService", npid, arrArgs.toArray(new String[0]) );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);

			if (nCode == 0) { // successful
				// values to pass to the web
				String strValue = joResult.get("value").toString();
				System.out.println("strValue : "+strValue);
				npid = (String)joResult.get("npid");

				JSONParser jpTemp = new JSONParser();
				JSONObject joInfo;
				String fee = "";
				String service_id = "";
				try {
					joInfo = (JSONObject)jpTemp.parse(strValue);
					if(strValue!=null) {
						fee = (String)joInfo.get("fee");
						service_id = (String)joInfo.get("service_id");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"fee\":\""+fee+"\", \"service_id\":\""+service_id+"\", \"npid\":\""+npid+"\" }";
			} else { // fail
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\" }";
			}
		}
		
		System.out.println("strOK : "+strOK);
		
		return strOK;
	}

	/*
	 * commitData
	 * addData Writes information to the chain.
	 */
	@RequestMapping("/svm/service/payFeeForReserve")
	@ResponseBody
	public String commitData(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String npid= StringUtil.nvl((String)map.get("npid"));
		String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");

		JSONObject joResult = null;
		if(arrArgs!=null) {
			joResult = CPWalletUtil.putValue(serviceId, ApiHelper.EC_CHAIN, "payFeeForReserve", npid, arrArgs.toArray(new String[0]) );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);
		}
		
		System.out.println("joResult : "+joResult);
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		return joResult.toString();
	}

}
