package io.nodehome.sample.web;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class SampleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);

	/*
	var sQuery = {"serviceID" : "" };
	$.ajax({
	    url: "http://localhost:8080/host/getSimpleHostList", 
	    type: 'POST',
	    data: JSON.stringify(sQuery),
	    dataType: 'json', 
		contentType:"application/json;charset=UTF-8",
	    success: function(data) { 
			if(data['result'] != "FAIL") {
				alert(JSON.stringify(data['list']));
			} else {
				alert();
				return false;
			}
	    },
	    error:function(data,status,er) { 
	        alert("error: "+data.responseText+" status: "+status+" er:"+er);
	    }
	});
	@RequestMapping("/wallet/createNewWallet")
	@ResponseBody
	public String createNewWallet(HttpServletRequest request, @RequestBody WalletVO vo, ModelMap model) throws UnknownHostException, IOException {

		String strWID=StringUtil.nvl(vo.getWid());
		~~~~~~~~~~~~
	}
	
	Or
	
	@RequestMapping("/wallet/createNewWallet")
	@ResponseBody
	public String createNewWallet(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) throws UnknownHostException, IOException {

		String strWID=StringUtil.nvl((String)map.get("wid"));
		String nCID=StringUtil.nvl((String)map.get("cid"));
		String strName=StringUtil.nvl((String)map.get("wname"));
		~~~~~~~~~~~~
	}
	
	*/
		
	
	
	
/*
* Platform, javascript Cross-Origin Resource Sharing between SVMs API for communication without trouble
* Calling api with json parameter and getting json return value
* request: {"requestUrl": "http://ds.nodehome.io/seedhost","serviceID":"nodehome","parameters":"~~~~~"}
* response: {"result": "OK", "list": "values ...."}
	 
	@RequestMapping("/svm/common/callCorsUrl")
	@ResponseBody
	public String callCorsUrl(@RequestBody HashMap<String,String> map) throws IOException {
		String requestUrl = "";
		HashMap<String,String> pMaps = new HashMap<String,String>();
		if(map!=null) {
			requestUrl = (String)map.get("requestUrl");
			map.remove("requestUrl");
		}
		
	    String JSONInput = new JSONObject(map).toString();
	    if(JSONInput==null || JSONInput.equals("")) JSONInput = "{}";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity param= new HttpEntity(JSONInput, headers);

	    RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.postForObject(requestUrl, param, String.class);

		return result;
	}
	<script>
	var sQuery = {"requestUrl" : "http://ds.nodehome.io/seedhost"};
	$.ajax({
	    url: "/svm/common/callCorsUrl", 
	    type: 'POST',
	    data: JSON.stringify(sQuery),
	    dataType: 'json', 
		contentType:"application/json;charset=UTF-8",
	    success: function(data) { 
			if(data['result'] != "FAIL") {
				alert(JSON.stringify(data['list']));
			} else {
				alert(data['result']);
				return false;
			}
	    },
	    error:function(data,status,er) { 
	        alert("error: "+data.responseText+" status: "+status+" er:"+er);
	    }
	});

	</script>
*/
	
	
	
	
	

	// Tie objects in json form
	@RequestMapping("/bbb")
	public ResponseEntity<HashMap> bbbb(ModelMap model) throws UnknownHostException, IOException {

		System.out.println("bbb  ");
		HashMap m = new HashMap();
		m.put("111", "111111111");
		m.put("222", "22222222");

		return new ResponseEntity<HashMap>(m, HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private String getResultJsonMessage(String result, String message) {
		JSONObject ret = new JSONObject();
		ret.put("result", result);
		ret.put("message", message);
		
		return ret.toJSONString();
	}
}
