package io.nodehome.svm.platform.web;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SvmTokenController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmTokenController.class);

	@RequestMapping("/svm/token/queryTokenBalance")
	@ResponseBody
	public String queryTokenBalance(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String strPID = "PID";
		String strVER = "10000";	
		String walletID= (String)map.get("walletID");
		String tokenId= StringUtil.nvl((String)map.get("tokenId"));
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"test");

		JSONObject joResult = null;
		if(strPID.equals("")) strPID = KeyManager.PID;
		if(strVER.equals("")) strVER = KeyManager.VERSION;

		joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "queryTokenBalance", new String[] {strPID, strVER, walletID, tokenId, "", "", ""}, chainID, netType);

		long nCode = (Long)joResult.getOrDefault("ec",-1L);

		if (nCode == 0) { // successful
			// values to pass to the web
			JSONArray strValue = (JSONArray)joResult.get("value");
			
			strOK = "{ \"result\":\"OK\", \"value\":"+strValue.toJSONString()+" }";
		} else { // fail
			// values to pass to the web
			strOK = "{ \"result\":\"FAIL\", \"value\":[] }";
		}
		
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		return strOK;
	}

	/*
	 * manager Wallet balance check.
	 */
	@RequestMapping("/svm/token/queryTokenTransHistory")
	@ResponseBody
	public String queryTokenTransHistory(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String strPID = KeyManager.PID;
		String strVER = KeyManager.VERSION;
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"test");
		arrArgs.add("");
		arrArgs.add("");
		arrArgs.add("");

		JSONObject joResult = null;
		if(strPID.equals("")) strPID = KeyManager.PID;
		if(strVER.equals("")) strVER = KeyManager.VERSION;

		joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "queryTokenTransHistory", arrArgs.toArray(new String[0]), chainID, netType);

		long nCode = (Long)joResult.getOrDefault("ec",-1L);

		if (nCode == 0) { // successful
			// values to pass to the web
			String strValue = joResult.get("value").toString();
			
			strOK = "{ \"result\":\"OK\", \"value\":"+strValue+" }";
		} else { // fail
			// values to pass to the web
			strOK = "{ \"result\":\"FAIL\", \"value\":{} }";
		}
		
		return strOK;
	}

	@RequestMapping("/svm/token/createToken")
	@ResponseBody
	public String createToken(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String npid= StringUtil.nvl((String)map.get("npid"));
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		JSONObject joResult = null;
		if(arrArgs!=null) {
			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "createToken", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);
		}
		
		System.out.println("joResult : "+joResult);
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		return joResult.toString();
	}

	@RequestMapping("/svm/token/mintToken")
	@ResponseBody
	public String mintToken(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String npid= StringUtil.nvl((String)map.get("npid"));
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		JSONObject joResult = null;
		if(arrArgs!=null) {
			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "mintToken", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);
		}
		
		System.out.println("joResult : "+joResult);
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		return joResult.toString();
	}
}

