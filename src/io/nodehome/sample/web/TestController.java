package io.nodehome.sample.web;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class TestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@RequestMapping("/test/{name}/ttt")
	public String key(ModelMap model,@PathVariable String name) throws UnknownHostException, IOException {

		System.out.println("name : " +name);

		return "test";
	}

	@RequestMapping("/aa")
	public String aaaa(ModelMap model) throws UnknownHostException, IOException {

		System.out.println("aa  ");

	    String JSONInput = ("{ \"aggs\":\"aggs\"} ");

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity param= new HttpEntity(JSONInput, headers);


	    RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.postForObject("http://ds.nodehome.io/seedhost", param, String.class);

	    System.out.println(result);
	    
		return "test";
	}
	
	// Tie objects in json form
	@RequestMapping("/bb")
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
	 

	
	

	/*
	public String createNewWallet(HttpServletRequest request, @RequestBody Map<String, Object> map, ModelMap model) throws UnknownHostException, IOException {
		System.out.println("----- "+map.get("kkk"));
		System.out.println("----- "+map.get("aid"));
	}
	
	@RequestMapping("/wallet/createNewWallet")
	@ResponseBody
	public String createNewWallet(HttpServletRequest request, @RequestBody WalletVO vo, ModelMap model) throws UnknownHostException, IOException {
		String strAID=EtcUtils.NullS(vo.getAid());
	}
	*/
	/*
	    $.ajax({
	    	url: self.links()[0].href,
	    	type: "POST",
	    	statusCode: {
	    	    200: function () {
	    	        //I always ended up here
	    	    },
	    	    303: function () {
	    	    }
	    	},
	    	complete: function (e, xhr, settings) {
	    	    if (e.status === 200) {
	    	        //..and then here
	    	    } else if (e.status === 303) {
	    	    } else {                           
	    	    }
	    	}
	    	
			$(function() {
			    $.ajax({
			        type : 'GET',
			        url : "http://www.redmine.org/trackers.json",
			        success : function(data) {
			            alert(data);
			        },
			        dataType : 'jsonp'
			    });
			});
*/
	    
}
