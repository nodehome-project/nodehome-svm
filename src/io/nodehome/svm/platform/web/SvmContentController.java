
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
public class SvmContentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmContentController.class);

	 /*
	  * Content add Data
	  */
	@RequestMapping("/svm/content/reserveSetData")
	@ResponseBody
	public String addData(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		String npid= (String)map.get("npid");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValue(serviceID, ApiHelper.EC_CHAIN, "reserveSetData", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);

			if (nCode == 0) { // successful
				// values to pass to the web
				String strValue = joResult.get("value").toString();
				System.out.println("strValue : "+strValue);
				npid = (String)joResult.get("npid");

				JSONParser jpTemp = new JSONParser();
				JSONObject joInfo;
				String fee = "";
				String reserve_id = "";
				try {
					joInfo = (JSONObject)jpTemp.parse(strValue);
					if(strValue!=null) {
						fee = (String)joInfo.get("fee");
						reserve_id = (String)joInfo.get("reserve_id");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"fee\":\""+fee+"\", \"reserve_id\":\""+reserve_id+"\", \"npid\":\""+npid+"\" }";
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
	@RequestMapping("/svm/content/payFeeForReserve")
	@ResponseBody
	public String commitData(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String npid= StringUtil.nvl((String)map.get("npid"));
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		JSONObject joResult = null;
		if(arrArgs!=null) {
			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "payFeeForReserve", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);
		}
		
		System.out.println("joResult : "+joResult);
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		return joResult.toString();
	}

	 /*
	  * Cancel add Data 
	  */
	@RequestMapping("/svm/content/removeUploadData")
	@ResponseBody
	public String removeUploadData(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		String npid= StringUtil.nvl((String)map.get("npid"));
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "removeUploadData", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);

			if (nCode == 0) { // successful
				strOK = "{ \"result\":\"OK\" }";
			} else { // fail
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\" }";
			}
		}
		
		System.out.println("strOK : "+strOK);
		
		return strOK;
	}

	/*
	 * get Contents Data List
	 */
	@RequestMapping("/svm/content/getSubDataList")
	@ResponseBody
	public String getDataList(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "getSubDataList", arrArgs.toArray(new String[0]), chainID, netType );
			
			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			if (nCode == 0) {
				String strValue = joResult.get("value").toString();
				if(strValue!=null) strValue = strValue.replaceAll("\\\\","");

				// Variable setting
				List<HashMap> recordList = new ArrayList<HashMap>(); 
				JSONParser jpTemp = new JSONParser();
				JSONArray records = null;
				JSONObject obj = null;			// value JSON Object
				JSONObject objRecord = null;	// records of value JSON Object
				int totalCount = 0;
				int startIndex = 0; 
				
				// Get List
				try {
					obj = (JSONObject)jpTemp.parse(strValue);

					if(obj!=null) {
						totalCount = (int)((long)obj.get("total"));
						startIndex = (int)((long)obj.get("start"));
						records = (JSONArray)obj.get("records");
						if(records!=null && records.size()>0) {
							for(int i=0; i<records.size(); i++) {
								objRecord = (JSONObject)records.get(i);
								JSONArray objKey = null; // records key list JSON Array
								JSONObject objData = (JSONObject)objRecord.get("data"); // data in records list JSON Object
								JSONObject objContent = (JSONObject)objData.get("content");
								objKey = (JSONArray)objRecord.get("key");
								
								String recordKey = (String)objKey.get(objKey.size()-1);	// Include the last key in the key array in the output data.
								HashMap<String, Object> recordMap = new HashMap<String, Object>();
								recordMap.put("recordKey", recordKey);

								recordMap.put("content_title", (String)objData.get("content_title"));
								recordMap.put("owner", (String)objData.get("owner"));
								recordMap.put("forsale", Boolean.toString((boolean)objData.get("forsale")));
								recordMap.put("content_id", (String)objData.get("content_id"));
								recordMap.put("group_id", (String)objData.get("group_id"));
								recordMap.put("keys", (JSONArray)objData.get("keys"));
								recordMap.put("sale_memo", (String)objData.get("sale_memo"));
								recordMap.put("content_price", (long)objData.get("content_price"));
								recordMap.put("timestamp", (long)objData.get("timestamp"));
								for(Iterator<?> iterator = objContent.keySet().iterator(); iterator.hasNext();) {
								    String key = (String) iterator.next();
								    recordMap.put(key, (String)objContent.get(key));
								}
								recordList.add(recordMap);
							}
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				JSONArray jsonArray = new JSONArray();
				jsonArray.addAll(recordList);

				System.out.println("*******************************************");
				System.out.println(jsonArray.toJSONString());
				System.out.println("*******************************************");
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"totalCount\":\""+totalCount+"\", \"startIndex\":\""+startIndex+"\", \"list\":"+jsonArray.toJSONString()+" }";
			} else {
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"totalCount\":\"0\", \"startIndex\":\"0\", \"list\":\"[]\" }";
			}
		}

		System.out.println("strOK : "+strOK);

		return strOK;
	}


	/*
	 * Provide accurate key information and retrieve the data stored in the chain.
	 * get Contents Data 
	 */
	@RequestMapping("/svm/content/getData")
	@ResponseBody
	public ResponseEntity<String> getData(@RequestBody HashMap<Object,Object> map, ModelMap model) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"FAIL\", \"nCode\":\"-1\", \"strValue\":\"\" }";
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValue(serviceID, ApiHelper.EC_CHAIN, "getData", arrArgs.toArray(new String[0]), chainID );
			
			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			if (nCode == 0) {
				String strValue = joResult.get("value").toString();
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"value\":"+strValue+" }";
			} else {
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"value\":\"\" }";
			}
		}

	    // Specifying response headers
	    HttpHeaders resHeaders = new HttpHeaders();
	    resHeaders.add("Content-Type", "application/json;charset=UTF-8");
	    return new ResponseEntity<String>(strOK, resHeaders, HttpStatus.CREATED) ;

		//System.out.println("strOK : "+strOK);
		//return strOK;
	}
	
	@RequestMapping("/svm/content/reserveSetContentOwner")
	@ResponseBody
	public String reserveSetContentOwner(@RequestBody HashMap<Object,Object> map, ModelMap model) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";
		
		String npid= (String)map.get("npid");
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.getValue(serviceId, ApiHelper.EC_CHAIN, "reserveSetContentOwner", npid, arrArgs.toArray(new String[0]), chainID );
			
			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			if (nCode == 0) {
				String strValue = joResult.get("value").toString();
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"value\":"+strValue+" }";
			} else {
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"value\":\"\" }";
			}
		}

		System.out.println("strOK : "+strOK);
		return strOK;
	}
	
	 /*
	  * Delete add Data 
	  */
	@RequestMapping("/svm/content/deleteData")
	@ResponseBody
	public String deleteData(@RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		String npid= StringUtil.nvl((String)map.get("npid"));
		List<Object> arrArgs=(ArrayList<Object>)map.get("parameterArgs");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "deleteData", npid, arrArgs.toArray(new String[0]), chainID );

			long nCode = (Long)joResult.getOrDefault("ec",-1L);

			if (nCode == 0) { // successful
				strOK = "{ \"result\":\"OK\" }";
			} else { // fail
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\" }";
			}
		}
		
		System.out.println("strOK : "+strOK);
		
		return strOK;
	}

	 /*
	  * Data validation
	  */
	@RequestMapping("/svm/content/dataValidation")
	@ResponseBody
	public String dataValidation(@RequestBody HashMap<String,Object> map) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"FAIL\", \"searchCount\":0 }";
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		if(map!=null) map.remove("serviceID");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"testnet");

	    String JSONInput = new JSONObject(map).toString();
	    if(JSONInput==null || JSONInput.equals("")) JSONInput = "{}";
	    JSONInput = JSONInput.replaceAll("\"", "\\\"");
	    JSONInput = "{\"selector\":"+JSONInput+",\"limit\": 5}";

	    JSONParser jpTemp = new JSONParser();
	    JSONObject joResult = null;
		joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "queryData", new String[]{KeyManager.PID, KeyManager.VERSION, JSONInput,"0","5","","",""}, chainID, netType );
		long nCode = (Long)joResult.getOrDefault("ec",-1L);
		int searchCount = 0;

		if (nCode == 0) { // successful
			joResult = (JSONObject)joResult.get("value");
			searchCount = (int)((long)joResult.get("total"));
			if(searchCount>0)
				strOK = "{ \"result\":\"OK\", \"searchCount\":"+searchCount+" }";
		} else { // fail
			// values to pass to the web
			strOK = "{ \"result\":\"FAIL\", \"searchCount\":0 }";
		}
	
		
		System.out.println("strOK : "+strOK);
		
		return strOK;
	}
	
	@RequestMapping("/svm/content/queryData")
	@ResponseBody
	public String queryData(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String strPID = KeyManager.PID;
		String strVER = KeyManager.VERSION;
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		arrArgs.add("");
		arrArgs.add("");
		arrArgs.add("");

		JSONObject joResult = null;

		joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "queryData", arrArgs.toArray(new String[0]), chainID, netType);

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

	
	/*
	  * infoBuyContent 
	  */
	@RequestMapping("/svm/content/infoBuyContent")
	@ResponseBody
	public String infoBuyContent(HttpServletRequest request, @RequestBody HashMap<Object,Object> map, ModelMap model, HttpServletResponse servletResponse) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";

		//String strPID = null;
		String strPID = KeyManager.PID;
		String strVER = KeyManager.VERSION;
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"testnet");
		arrArgs.add("");
		arrArgs.add("");
		arrArgs.add("");

		JSONObject joResult = null;

		joResult = CPWalletUtil.getValueManager(serviceID, ApiHelper.EC_CHAIN, "infoBuyContent", arrArgs.toArray(new String[0]), chainID, netType);

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
	
	/*
	  * infoBuyContent 
	  */
	@RequestMapping("/svm/content/setContentOwner")
	@ResponseBody
	public String setContentOwner(@RequestBody HashMap<Object,Object> map, ModelMap model) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";
		
		String npid= (String)map.get("npid");
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "setContentOwner", npid, arrArgs.toArray(new String[0]), chainID );
			
			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			if (nCode == 0) {
				String strValue = joResult.get("value").toString();
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"value\":"+strValue+" }";
			} else {
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"value\":\"\" }";
			}
		}

		System.out.println("strOK : "+strOK);
		return strOK;
	}
	
	
	/*
	  * BuyContent 
	  */
	@RequestMapping("/svm/content/buyContent")
	@ResponseBody
	public String buyContent(@RequestBody HashMap<Object,Object> map, ModelMap model) throws UnknownHostException, IOException {
		String strOK = "{ \"result\":\"OK\" }";
		
		String npid= (String)map.get("npid");
		List<String> arrArgs=(ArrayList<String>)map.get("parameterArgs");
		String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
		if(arrArgs!=null) {
			JSONObject joResult = null;

			joResult = CPWalletUtil.putValue(serviceID, ApiHelper.EC_CHAIN, "buyContent", npid, arrArgs.toArray(new String[0]), chainID );
			
			long nCode = (Long)joResult.getOrDefault("ec",-1L);
			if (nCode == 0) {
				String strValue = joResult.get("value").toString();
				strOK = "{ \"result\":\"OK\", \"nCode\":\""+nCode+"\", \"value\":"+strValue+" }";
			} else {
				// values to pass to the web
				strOK = "{ \"result\":\"FAIL\", \"nCode\":\""+nCode+"\", \"value\":\"\" }";
			}
		}

		System.out.println("strOK : "+strOK);
		return strOK;
	}
}

/*	// ArrayList sort
 * http://codeman77.tistory.com/4

package test;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
 
class Point {
    private String name;
    private Integer point;
 
    public Point(String name, Integer point) {
        this.name = name;
        this.point = point;
    }
 
    public String getName() {
        return name;
    }
 
    public Integer getPoint() {
        return point;
    }
 
    @Override
    public String toString() {
        return "(" + name + "," + point + ")";
    }
 
}
 
public class ArrayList_ sort_application {
    public static void main(String[] args) {
 
        ArrayList<point> arrayList = new ArrayList<point>();
 
        // (k, 30) (w, 20) (a, 50) (z, 40) (f, 0) (g, 90)
        arrayList.add(new Point("k", 30));
        arrayList.add(new Point("w", 20));
        arrayList.add(new Point("a", 50));
        arrayList.add(new Point("z", 40));
        arrayList.add(new Point("f", 0));
        arrayList.add(new Point("g", 90));
 
        for (Point p : arrayList) {
            System.out.print(p);
        }
        System.out.println();
 
        // Sort Descending
        DescendingObj descending = new DescendingObj();
        Collections.sort(arrayList, descending);
 
        System.out.print("Name Descending - ");
        for (Point p : arrayList) {
            System.out.print(p);
        }
        System.out.println();
 
        // Sort Ascending
        AscendingObj ascending = new AscendingObj();
        Collections.sort(arrayList, ascending);
 
        System.out.print("Point Ascending - ");
        for (Point p : arrayList) {
            System.out.print(p);
        }
        System.out.println();
    }
 
}
 
// String Descending
class DescendingObj implements Comparator<point> {
 
    @Override
    public int compare(Point o1, Point o2) {
        return o2.getName().compareTo(o1.getName());
    }
 
}
 
// Integer Ascending
class AscendingObj implements Comparator<point> {
 
    @Override
    public int compare(Point o1, Point o2) {
        return o1.getPoint().compareTo(o2.getPoint());
    }
 
}
*/