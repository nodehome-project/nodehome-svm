package io.nodehome.svm.platform.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.cmm.util.FileUtil;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.biz.BlacklistVO;
import io.nodehome.svm.common.util.DateUtil;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class SvmHostController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmHostController.class);

	private String SEED_HOST = GlobalProperties.getProperty("project_seedHost");
	private String fileSparator = System.getProperty("file.separator");
	private String relativePathPrefix = GlobalProperties.class.getResource("").getPath().substring(0, GlobalProperties.class.getResource("").getPath().lastIndexOf("classes"));
	private String hostPropertiesPath = relativePathPrefix + "hosts" + fileSparator + "";
	
	/* host list views (50)
	 * 
	 * result parameter
	 * ACCEPT		service 		list
	 * LIST			no service		list
	 * FAIL
	 */
	@RequestMapping("/host/getHostList")
	@ResponseBody
	public String getHost(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) throws UnknownHostException, IOException {
    	String returnMsg = "";
    	String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
    	String strFail = "{ \"result\":\"FAIL\" }";
    	String strResultGubun = "ACCEPT";

    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

    	String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	String[] hosts = str.split("\n");				// file original data list
			String[] hostList = null;
        	
        	try {
        		String[] localHostList = new String[(hosts.length)];
        		Vector<String> updateHostList = new Vector<String>();
        		int renewalIndex = 0;

        		// get local file host list
    			for(int s=0; s<hosts.length; s++) {	// Host list			
    				if(hosts[s]!=null && !hosts[s].equals("") && !(hosts[s].substring(0, 1)).equals("#") && !(hosts[s]).equals(SEED_HOST)) {	// Comments or seed host addresses are excluded from the calculation.
    					localHostList[renewalIndex++] = StringUtil.nvl(hosts[s].toString());
    				}
    			}
    			
        		// If it has been more than one month since the update, / file list update processing - START
            	String fileDate = hosts[0].toString().replaceAll("# ","");
            	String preMonth = DateUtil.getPlusMinusDate(DateUtil.getDate("yyyyMMddHHmmss"), -30, "yyyyMMddHHmmss");	// Renew files once a month
            	if(Double.parseDouble(fileDate) < Double.parseDouble(preMonth)) {
            		for(int s=0; s<localHostList.length; s++) updateHostList.add(localHostList[s]);
            		
        			for(int s=0; s<localHostList.length; s++) {
    			    	Random generator = new Random();
    			        int num = generator.nextInt(localHostList.length);
    			        
    		        	String[] host = (StringUtil.nvl(localHostList[num]).trim()).split(Pattern.quote("|"));
        				String tUrl = host[0];
        				if(tUrl!=null && !tUrl.equals("") && (StringUtil.nvl(tUrl)).indexOf(localServiceHost)==-1) {
                    		String tempHosts = urlConnection(tUrl+"/host/getSimpleHostList","{\"serviceId\":\""+serviceId+"\"}");

                    		if(!tempHosts.equals("")) {

                        		JSONParser paRes = new JSONParser();
            					JSONObject joRes = (JSONObject) paRes.parse(tempHosts);
            					String result = (String)joRes.get("result");

            					if(result.equals("ACCEPT") || result.equals("LIST")) {
            						Vector<String> tempHostList = new Vector<String>();
            						JSONArray hostArrary = (JSONArray) paRes.parse(joRes.get("list").toString()) ;
            						if(hostArrary!=null && hostArrary.size()>0) {
        	    						for(int i=0; i<hostArrary.size(); i++) {
                	    					if(!(StringUtil.nvl(hostArrary.get(i),"")).substring(0, 1).equals("#")) {
                	    						tempHostList.add(StringUtil.nvl(hostArrary.get(i),""));
                	    					}
        	    						}
                					}
            						updateHostList = tempHostList;
                					paRes = null;
                					break;
            					}
                    		}
        				}
        			}

        			if(updateHostList!=null) {
        				hostList = new String[updateHostList.size()];
        				for(int i=0; i<updateHostList.size(); i++) {
        					hostList[i] = updateHostList.get(i);
        				}
        			}

        			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n";
					for(int i=0; i<hostList.length; i++) {
						String tUrl = StringUtil.nvl(hostList[i]).trim();
						if(!(tUrl).equals(""))
							writeContent += StringUtil.nvl(hostList[i],"")+"\n";
					}
					FileUtil.writeFile(sroot, "utf-8", false, writeContent);
            	} else {
            		
            		hostList = localHostList;
            	}
        		// If it has been more than one month since the update, / file list update processing - END

            	// return message
            	if((hosts[0].toString()).indexOf("noService")>-1) strResultGubun = "LIST";
    			if(hostList.length>0) {
    				String returnUrl = "";
    				for(int s=0; s<hostList.length; s++) {
    					
        				String tUrl = StringUtil.nvl(hostList[s]).trim();
    					if(!tUrl.equals("") && !(StringUtil.nvl(hostList[s],"")).substring(0, 1).equals("#")) {
    						returnUrl+= "\"" + hostList[s].toString() + "\",";
    					}
    				}
    				
    				if(returnUrl.length()>0) returnUrl = returnUrl.substring(0,returnUrl.length()-1);
    				returnMsg = "{ \"result\":\""+strResultGubun+"\",\"list\":["+returnUrl+"] }";
    			} else {
    				
    				returnMsg = strFail.toString();
    			}

        	} catch(Exception e) {
				e.printStackTrace();
        		returnMsg = strFail.toString();
        	}
        	
    	} else {
    		returnMsg = strFail.toString();
    	}

    	return returnMsg.toString();
    }

	/*
	 * Simple lookup
	 */
	@RequestMapping("/host/getSimpleHostList")
	@ResponseBody
	public String getSimpleHostList(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) throws UnknownHostException, IOException {
    	String returnMsg = "";
    	String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
    	String strFail = "{ \"result\":\"FAIL\" }";
    	String strResultGubun = "ACCEPT";

    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

    	String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	String[] hosts = str.split("\n");				// data list
        	
        	if((hosts[0].toString()).indexOf("noService")>-1) strResultGubun = "LIST";
        	
    		if(str!=null && str.length()>0) {
    			if(hosts.length>0) {
    				hosts = StringUtil.RandomizeArray(hosts);

    				String returnUrl = "";
    				for(int s=0; s<hosts.length; s++) {
        				String tUrl = StringUtil.nvl(hosts[s]).trim();
    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
        					returnUrl+= "\"" + hosts[s].toString() + "\",";	
    					}
    				}
    				
    				if(returnUrl.length()>0) returnUrl = returnUrl.substring(0,returnUrl.length()-1);
    				returnMsg = "{ \"result\":\""+strResultGubun+"\",\"list\":["+returnUrl+"] }";
    			} else {
    				returnMsg = strFail.toString();
    			}
    		} else {
				returnMsg = strFail.toString();
    		}
    	} else {
    		returnMsg = strFail.toString();
    	}

    	return returnMsg;
    }

	// Function to request host addition
	// add manager invokes
	// URL invoked from SApp web page
	@RequestMapping("/host/addHostProcess")
    public @ResponseBody String addRequest(HttpServletRequest request, ModelMap model) {
		String returnMsg = "";
    	String strFail = "{ \"result\":\"FAIL\" }";
    	String svrCd = StringUtil.nvl(request.getParameter("serviceId"),GlobalProperties.getProperty("project_serviceid"));

    	ServletContext context = request.getServletContext();
    	String sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "host_"+svrCd+".properties";

    	String localServiceHost = request.getRequestURL().toString();
    	String localServiceIp = request.getRemoteAddr();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
    	String mport = StringUtil.nvl(request.getParameter("mport"));
    	String mdomain = StringUtil.nvl(request.getParameter("mdomain"));
    	String mip = StringUtil.nvl(request.getParameter("mip"));
    	
		String tempHosts = "";
		try {
			tempHosts = ApiHelper.postJSON(""+SEED_HOST+"/requestAddHost", "{\"serviceId\":\""+svrCd+"\",\"mdomain\":\""+mdomain+"\",\"mip\":\""+mip+"\",\"mport\":\""+mport+"\",\"requestUrl\":\""+localServiceHost+"|"+localServiceIp+"\"}");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//System.out.println("tempHosts  : "+tempHosts);
		if(tempHosts!=null && !tempHosts.equals("")) {
			JSONParser paRes = new JSONParser();
			// Write local server properties file.
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n";
			try {
				JSONObject joRes = (JSONObject) paRes.parse(tempHosts);
				String result = (String)joRes.get("result");
				if(result.equals("ACCEPT") || result.equals("LIST")) {
					JSONArray hostArrary = (JSONArray) paRes.parse(joRes.get("list").toString()); 	// host list from server seed host
					for(int i=0; i<hostArrary.size(); i++) {
						if(!localServiceHost.equals((StringUtil.nvl(hostArrary.get(i),"")).toString())) {	
							writeContent += StringUtil.nvl(hostArrary.get(i),"")+"\n";
						}
					}
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
		}
		
		String result = "success";
		String message = "";
		return getResultJsonMessage(result, message);
	}
	
	/*
	 * host API to receive registration request
	 * If verification is successful, register host url in local file
	 */
	@RequestMapping("/host/requestAddHost")
    public @ResponseBody String requestAddHost(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String result = "OK";
		String message = "";

    	String remoteAddessIp = StringUtil.nvl(map.get("remoteAddessIp"),"");
    	String mport = StringUtil.nvl(map.get("mport"),"");
    	String serviceId = StringUtil.nvl(map.get("serviceId"),GlobalProperties.getProperty("project_serviceid"));
    	String requestUrl = StringUtil.nvl(map.get("requestUrl"),"");
    	if(requestUrl.equals("")) getResultJsonMessage("FAIL", "empty url");
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
    	
    	String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");

    	boolean scChk = false;
    	// Request host url Security validation *********************************************************************************** ****************************************** - START
    	String returnMsg = checkBlacklist(request, localServiceHost, serviceId, requestUrl, mport);
    	System.out.println("Security validation returnMsg : "+returnMsg);
    	if(returnMsg.indexOf(BlacklistVO.NORMAL)>-1) {
    		scChk = true;
    	}
        // Request host url Security validation *********************************************************************************** ****************************************** - END
    	
    	// Record in local host list file - START
    	if(scChk) {
        	String str = FileUtil.roadLocalFile(sroot);
			try {
	        	String oristr = StringUtil.nvl(str);
	        	if(oristr!=null && oristr.length()>0) {
	        		if((oristr.substring(0,1)).equals("#")) {
	        			oristr = oristr.substring(oristr.indexOf("\n"));
	        		}
					FileUtil.writeFile(sroot, "utf-8", true, "# "+DateUtil.getDate("yyyyMMddHHmmss")+"" + oristr+"\n" +(requestUrl+"|"+remoteAddessIp+"|"+mport) );	
	        	} else {
	        		FileUtil.writeFile(sroot, "utf-8", true, "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n" + (requestUrl+"|"+remoteAddessIp+"|"+mport) );	
	        	}
			} catch (Exception e) {
				result = "FAIL";
				e.printStackTrace();
			}	
    	} else {
			System.out.println(requestUrl + " Verification Fail");
    		result = "FAIL";
    		message = "Verification Fail";
    	}
    	// Record in local host list file - END
    	
		return getResultJsonMessage(result, message);
	}

	@RequestMapping("/host/testBlackList")
    public @ResponseBody String testBlackList(HttpServletRequest request, ModelMap model) {
		String result = "OK";
		String message = "";

//		BlacklistVO.initList();
//    	String mport = "8886";
//    	String serviceId = "nodehome";
//    	String requestUrl = "http://test-nodehome2.nodehome.io";
//    	if(requestUrl.equals("")) getResultJsonMessage("FAIL", "empty url");
//    	String localServiceHost = request.getRequestURL().toString();
//    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
//    	
//    	boolean scChk = false;
//    	// Request host url Security validation *********************************************************************************** ****************************************** - START
//    	String returnMsg = checkBlacklist(request, serviceId, requestUrl, mport);
//    	if(returnMsg.indexOf(BlacklistVO.ABNORMAL)<0 && returnMsg.indexOf("FAIL")<0) {
//    		scChk = true;
//    	}
//        // Request host url Security validation *********************************************************************************** ****************************************** - END
    	
		return getResultJsonMessage(result, message);
	}
	
	@RequestMapping("/setup")
    public String setup(HttpServletRequest request, ModelMap model) {
    	String serviceId = StringUtil.nvl(request.getParameter("serviceid"),GlobalProperties.getProperty("project_serviceid"));
    	if(serviceId.equals("")) serviceId = GlobalProperties.getProperty("project_serviceid");
    	 
    	String sroot = hostPropertiesPath.replaceAll("\\\\", "/")+"wallet_"+serviceId+".properties";

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	str = str.trim().replaceAll(" ","");
    		model.addAttribute("walletId",str);
    	}
    	
    	sroot = hostPropertiesPath+"host_"+serviceId+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	if(str!=null && str.length()>0) {
            	String[] hosts = str.split("\n");
            	String[] hostList = new String[hosts.length];

        		if(hosts!=null) {
        			int hi = 0;
        			for(int s=0; s<hosts.length; s++) {
        				if( hosts[s]!=null && hosts[s].length()>0 
        					&& !(hosts[s].substring(0, 1)).equals("#") && !(hosts[s]).equals(SEED_HOST)) {	// Comments or seed host addresses are excluded from the calculation.

        					// Duplicate checks for checked hosts
        					boolean chkValue = false;
        					for(int j=0; j<hostList.length; j++) {
        						if(hostList[j]!=null && (hostList[j]).equals((String)hosts[s].toString())) chkValue = true;
        					}
        					if(!chkValue) {
        						hostList[hi] = ((String)hosts[s].toString());
        						hi++;	
        					}
        				}
        			}
        		}
        		
        		for(int i=0; i<hostList.length; i++) {
        			if(hostList[i]==null) hostList[i] = "";
        		}
        		model.addAttribute("hostList",hostList);
        	}
    	}

		model.addAttribute("serviceId",serviceId);
    	return "svm/host/setup";
	}
	
	@RequestMapping(value = "/host/setupUpdateWid")
	public @ResponseBody String setupUpdateWid(HttpServletRequest request, @RequestParam Map<String, Object> param, ModelAndView mav) throws Exception {
    	ServletContext context = request.getServletContext();
		String wid = request.getParameter("wid");
		String svrCd = request.getParameter("serviceId");

    	String sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "wallet_"+svrCd+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
		FileUtil.writeFile(sroot, "utf-8", false, wid);

		String result = "success";
		String message = ""; 
		return getResultJsonMessage(result, message);
	}
	
	public String urlConnection(String connUrl) {
		String returnList = "";
        String pageContents = "";
        StringBuilder contents = new StringBuilder();

        try{
            URL url = new URL(connUrl);
            URLConnection con = (URLConnection)url.openConnection();
            InputStreamReader reader = new InputStreamReader (con.getInputStream(), "utf-8");
 
            BufferedReader buff = new BufferedReader(reader);
 
            while((pageContents = buff.readLine())!=null){
                contents.append(pageContents);
            }
            returnList = contents.toString();
            buff.close();
        }catch(FileNotFoundException e){
            return "{ \"result\":\"CONNECT FAIL\" } ";
        }catch(Exception e){
        	return "{ \"result\":\"FAIL\" } ";
        }
        return returnList;
	}
	
	public String urlConnection(String requestUrl, String map) throws IOException {
	    String JSONInput = map;
	    if(JSONInput==null || JSONInput.equals("")) JSONInput = "{}";

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> param= new HttpEntity<String>(JSONInput, headers);

		//System.out.println("SvmHostController.java requestUrl "+requestUrl);
		//System.out.println("SvmHostController.java 1");
	    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
	    String result = "";
	    try {
		    result = restTemplate.postForObject(requestUrl, param, String.class);
	    } catch (RestClientException e) {
	        ;
	        //e.printStackTrace();
	    }
	    //System.out.println("SvmHostController.java 2");

		return result;
	}
	private ClientHttpRequestFactory clientHttpRequestFactory() {
	    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	    factory.setReadTimeout(2000);
	    factory.setConnectTimeout(2000);
	    return factory;
	}   
	
	@SuppressWarnings("unchecked")
	private String getResultJsonMessage(String result, String message) {
		JSONObject ret = new JSONObject();
		ret.put("result", result);
		ret.put("message", message);
		
		return ret.toJSONString();
	}

	public String readPostData(HttpServletRequest request) {
		InputStream is;
		try 
		{
			is = request.getInputStream();

		    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	
		    StringBuffer sbLines = new StringBuffer();
		    String strLine = "";
	
			while((strLine = br.readLine()) != null) {
					strLine = br.readLine();
					sbLines.append(strLine);
			}
			return sbLines.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * *********************************************************************** Blacklist management process
	 */

	/*
	 * view에서 호출될 수 있도록 api로 제공한다. 
	 * 정기적 검사를 위한 api
	 */
	@RequestMapping("/eventCheckNode")
    public @ResponseBody String eventCheckNode(HttpServletRequest request, @RequestBody HashMap<String,String> map) {
		String result = "FAIL";
		String message = "";

		BlacklistVO.initList();
    	String mport = StringUtil.nvl(map.get("mport"));
    	String serviceId = StringUtil.nvl(map.get("serviceId"), GlobalProperties.getProperty("project_serviceid"));
    	String requestUrl = StringUtil.nvl(map.get("requestUrl"));
    	String targetUrl = StringUtil.nvl(map.get("targetUrl"));
    	if(requestUrl.equals("")) getResultJsonMessage("FAIL", "empty url");
    	System.out.println("requestVerification targetUrl : "+targetUrl);
    	
    	String returnMsg = checkBlacklist(request, requestUrl, serviceId, targetUrl, mport);
    	if(returnMsg.indexOf(BlacklistVO.ABNORMAL)>-1) {
    		result = "FAIL";
    		message = "Fake host.";
    	} else if(returnMsg.indexOf(BlacklistVO.NORMAL)>-1) {
    		result = "OK";
    		message = "Normal host.";
    	}
    	
		return getResultJsonMessage(result, message);
	}

	@RequestMapping("/checkNode")
	@ResponseBody
    public String checkNode(HttpServletRequest request) {
		String strRespone = "";
		try {
			//out.clear();
			String sBody = readRequest(request.getReader());
			String urlNodeM = "http://127.0.0.1:" + GlobalProperties.getProperty("nodem_port") + "/nodem.bin";
			strRespone = ApiHelper.postJSON(urlNodeM, sBody);
			//out.write(strRespone);
		} catch (IOException e1) {
			e1.printStackTrace();
			strRespone = "{\"ec\":5 ,\"Pid\":\"pid\",\"value\":{},\"ref\":\"Unknown response\"}";
			//out.write("{\"ec\":5 ,\"Pid\":\"pid\",\"value\":\"{}\",\"ref\":\"Unknown response\"}");
		}
		return strRespone.toString();
	}
	String readRequest(BufferedReader brReq) throws IOException {
		StringBuffer sbBuffer = new StringBuffer();
		String sLine;
		while ((sLine = brReq.readLine()) != null) {
			sbBuffer.append(sLine);		
		}	
		return sbBuffer.toString();
	}
	
    public String checkBlacklist(HttpServletRequest request, String localServiceHost, String serviceId, String verifiHost, String mport) {
		String returnMsg = "{ \"result\":\"OK\" , \"message\":\""+BlacklistVO.NORMAL+"\" }";

    	String remoteAddessIp = request.getRemoteAddr();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
    	String verifiResult = "";
    	String tempHosts = "";
    	
		/*
		 ********************************* node manager 검증 프로세스 시작
		 * 
		 * node 검증 처리 결과 코드
		 * 	0	 성공 	성공
			1	* 에러 내용 알수 없음	회손추정
			2	호출오류
			3	Json 파싱에 실패했다. 자신의 Sapp Hash를 구하는데 실패함 	호출오류
			4	대상 nodem으로 연결 실패 (url,port등이 틀렸을수 있다.)	통신장애
			5	* 대상  nodem에서의 응답값을 해석할 수 없음(파라메터 형식 / 규칙이 맞지 않음), 	회손추정
			6	서비스 아이디가 다르게 호출되었다.	호출오류
			7	* Sapp 을 찾지 못함	회손추정
			8	* Sapp의 사이트 hash값이 맞지 않는다. 	회손추정
			9	호출오류
			10	호출오류
			11	파라메터 수가 맞지 않음	호출오류
			12	파라메터 url을 해석하는 데 실패함, nodem 포트번호 해석 실패, nonce문자열이 10보다 작다.	호출오류
			13	호출오류
			14	호출오류
			15	호출오류
			16	호출오류
			17	로칼에서 호출되지 않았다. 자기자신을 검증하려고 시도했다. 호출오류
			18	호출오류
			19	* 응답 값의 Signature 가 맞지 않는다.	회손추정
		 */
	
    	JSONObject nodemRes = null;
    	JSONParser parser = new JSONParser();

		String localServiceHost2 = localServiceHost;
		try {
			String protocol = localServiceHost2.substring(0,localServiceHost2.indexOf("://")+3);
			localServiceHost2 = localServiceHost2.substring(localServiceHost2.indexOf("://")+3);
			if(localServiceHost2.indexOf(":")>-1)
				localServiceHost2 = localServiceHost2.substring(0,localServiceHost2.indexOf(":")-1);
			
			System.out.println("nodem check ready ");
			tempHosts = ApiHelper.postJSON(protocol+localServiceHost2+":"+GlobalProperties.getProperty("nodem_port")+"/nodem.bin", "{ \"cmd\":\"REQ_SN_checknode\" , \"pid\":\"pid\",\"ver\":10000, \"args\":[\""+localServiceHost+"\" ,\""+serviceId+"\", \""+verifiHost+"\" ,\""+mport+"\"] }");
			System.out.println(localServiceHost2 + " tempHosts : "+tempHosts);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(tempHosts!=null && !tempHosts.equals("")) {
			try {
				nodemRes = (JSONObject)parser.parse(tempHosts);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//System.out.println("nodem tempHosts : "+String.valueOf(nodemRes.get("ec")));

			long nCode = (Long)nodemRes.getOrDefault("ec",-1);
			if(nCode == 0) {
				verifiResult = BlacklistVO.NORMAL;
			} else if(nCode==1 || nCode==5 || nCode==7 || nCode==8 || nCode==19) {
				verifiResult = BlacklistVO.ABNORMAL;
				returnMsg = "{ \"result\":\""+BlacklistVO.ABNORMAL+"\" , \"message\":\"Hacked Sources\" }";
			} else {
				returnMsg = "{ \"result\":\"FAIL\" , \"message\":\"Verification Fail\" }";
			}
		}
		/*
		 ********************************* node manager 검증 프로세스 끝
		 */
		
		// [ 검사 결과 메모리 저장 ]
		System.out.println("검증 결과 verifiResult serviceId : "+serviceId);
		BlacklistVO.setCheckResult(serviceId, remoteAddessIp, verifiHost, verifiResult);
		
		List hostList = new ArrayList();
		System.out.println("검증 결과 verifiResult : "+verifiResult);
		if(verifiResult.equals(BlacklistVO.ABNORMAL)) {
			System.out.println("verifiResult : " +verifiResult);
			String targetHostStr = "";
			String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {
	    				for(int s=0; s<hosts.length; s++) {
	        				String tUrl = StringUtil.nvl(hosts[s]).trim();
	    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
	    						if(tUrl.indexOf(verifiHost)>-1) {
	    							targetHostStr = tUrl;
	    						} else {
	    							hostList.add(tUrl);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	file = null;
	    	
			// [ blacklist 파일 등록 ]
	    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";
	    	//System.out.println("sroot : "+sroot);

	    	file = new File(sroot);
	    	if(!file.exists()) {
	    		try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
        	String str = FileUtil.roadLocalFile(sroot);
			try {
				FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// [********** host list에서 제거 **********]
			sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}

			// [ Send noti to seed host ]
			tempHosts = "";
			try {
				tempHosts = ApiHelper.postJSON(""+SEED_HOST+"/notiBlacklist", "{\"serviceId\":\""+serviceId+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+verifiResult+"\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// [ broadcasting ]
			sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");
	    	
	    	file = new File(sroot);
	    	if(file.exists()) {
	        	str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {
	    				String returnUrl = "";
	    				for(int s=0; s<hosts.length; s++) {
	    					String hUrl = StringUtil.nvl(hosts[s],"");
	    					if(!hUrl.equals("") && !hUrl.substring(0, 1).equals("#") && hUrl.indexOf(verifiHost)<0 && hUrl.indexOf(localServiceHost)<0) {	// 검증 host, 본인 host를 제외한 모든 host들에 전파.
		        				String[] tUrl = hUrl.split(Pattern.quote("|"));
		        				tempHosts = "";
		        				try {
		        					System.out.println(tUrl[0] + "에 검증 요청!");
		        					tempHosts = ApiHelper.postJSON(""+tUrl[0]+"/host/requestBlockHost", "{\"reporterHost\":\""+localServiceHost+"\",\"serviceId\":\""+serviceId+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+verifiResult+"\",\"mport\":\""+mport+"\"}");
		        					System.out.println(tUrl[0] + "에 검증 결과 : "+tempHosts);
		        				} catch (IOException e1) {
		        					//e1.printStackTrace();
		        				}
	    					}
	    				}
	    			}
	    		}
	    	}
		} 
		
		return returnMsg;
	}

    // 검사만 하고 broadcasting 없음
    public String checkBlacklist2(HttpServletRequest request, String serviceId, String verifiHost, String mport) {
		String returnMsg = "{ \"result\":\"FAIL\" , \"message\":\"Connection Fail\" }";

    	String remoteAddessIp = request.getRemoteAddr();
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
    	String verifiResult = "";
    	String tempHosts = "";
    	
		/*
		 ********************************* node manager 검증 프로세스 시작
		 * 
		 * node 검증 처리 결과 코드
		 * 	0	 성공 	성공
			1	* 에러 내용 알수 없음	회손추정
			2	호출오류
			3	Json 파싱에 실패했다. 자신의 Sapp Hash를 구하는데 실패함 	호출오류
			4	대상 nodem으로 연결 실패 (url,port등이 틀렸을수 있다.)	통신장애
			5	* 대상  nodem에서의 응답값을 해석할 수 없음(파라메터 형식 / 규칙이 맞지 않음), 	회손추정
			6	서비스 아이디가 다르게 호출되었다.	호출오류
			7	* Sapp 을 찾지 못함	회손추정
			8	* Sapp의 사이트 hash값이 맞지 않는다. 	회손추정
			9	호출오류
			10	호출오류
			11	파라메터 수가 맞지 않음	호출오류
			12	파라메터 url을 해석하는 데 실패함, nodem 포트번호 해석 실패, nonce문자열이 10보다 작다.	호출오류
			13	호출오류
			14	호출오류
			15	호출오류
			16	호출오류
			17	로칼에서 호출되지 않았다. 자기자신을 검증하려고 시도했다. 호출오류
			18	호출오류
			19	* 응답 값의 Signature 가 맞지 않는다.	회손추정
		 */
	
    	JSONObject nodemRes = null;
    	JSONParser parser = new JSONParser();

		String localServiceHost2 = localServiceHost;
		try {
			String protocol = localServiceHost2.substring(0,localServiceHost2.indexOf("://")+3);
			localServiceHost2 = localServiceHost2.substring(localServiceHost2.indexOf("://")+3);
			if(localServiceHost2.indexOf(":")>-1)
				localServiceHost2 = localServiceHost2.substring(0,localServiceHost2.indexOf(":")-1);
			
			tempHosts = ApiHelper.postJSON(protocol+localServiceHost2+":"+GlobalProperties.getProperty("nodem_port")+"/nodem.bin", "{ \"cmd\":\"REQ_SN_checknode\" , \"pid\":\"pid\",\"ver\":10000, \"args\":[\""+localServiceHost+"\" ,\""+serviceId+"\", \""+verifiHost+"\" ,\""+mport+"\"] }");
			System.out.println("tempHosts : "+tempHosts);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(tempHosts!=null && !tempHosts.equals("")) {
			try {
				nodemRes = (JSONObject)parser.parse(tempHosts);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//System.out.println("nodem tempHosts : "+String.valueOf(nodemRes.get("ec")));

			long nCode = (Long)nodemRes.getOrDefault("ec",-1);
			System.out.println("tempHosts nCode : "+nCode);
			if(nCode == 0) {
				verifiResult = BlacklistVO.NORMAL;
				returnMsg = "{ \"result\":\"OK\" , \"message\":\"Verification OK\" }";
			} else if(nCode==1 || nCode==5 || nCode==7 || nCode==8 || nCode==19) {
				verifiResult = BlacklistVO.ABNORMAL;
				returnMsg = "{ \"result\":\""+BlacklistVO.ABNORMAL+"\" , \"message\":\"Hacked Sources\" }";
			} else {
				returnMsg = "{ \"result\":\"FAIL\" , \"message\":\"Verification Fail\" }";
			}
		}
		/*
		 ********************************* node manager 검증 프로세스 끝
		 */
		
		// [ 검사 결과 메모리 저장 ]
		BlacklistVO.setCheckResult(serviceId, remoteAddessIp, verifiHost, verifiResult);
		
		return returnMsg;
	}
    
	/*
	 * blacklist 등록 요청 전파
	 * 최초 신고SApp이 다른 Sapp 에 blacklist요청 할떄 호출. 신고 받은 내용 검증 후 blacklist 처리하고 seed server에 notification.
	 */
	@RequestMapping("/host/requestBlockHost")
	@ResponseBody
    public String requestBlockHost(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String returnMsg = "{ \"result\":\"OK\" }";
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
    	System.out.println("요청 받음 /host/requestBlockHost - localServiceHost : "+localServiceHost);

    	String remoteAddessIp = request.getRemoteAddr();	
    	String reporterHost = StringUtil.nvl(map.get("reporterHost")); // 최초 요청자 host
    	
    	String serviceId = StringUtil.nvl(map.get("serviceId"));	// service id
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String mport = StringUtil.nvl(map.get("mport"));
    	if(serviceId.equals("")) return "{ \"result\":\"FAIL\" }";

		// [********** 검증 **********]
		/*
		 * node manager 검증 프로세스 시작
		 */
		String result = ""; // normal / abnormal 판정
    	JSONParser paRes = new JSONParser();
		returnMsg = checkBlacklist2(request, serviceId, verifiHost, mport);	// Verification 
		try {
			JSONObject joRes = (JSONObject) paRes.parse(returnMsg);
			result = String.valueOf(joRes.get("result"));	
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		/*
		 * node manager 검증 프로세스 끝
		 */
		
		// [********** 검사 결과 메모리 저장 **********]
		if(result.equals(BlacklistVO.NORMAL) || result.equals(BlacklistVO.ABNORMAL))
			BlacklistVO.setCheckResult(serviceId, remoteAddessIp, verifiHost, result);
		
		// [********** 검증결과가 비정상일떄만 blacklist에 등록 **********]
		String sroot = "";
		String targetHostStr = "";
    	List hostList = new ArrayList();
		sroot = hostPropertiesPath+"host_"+serviceId+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	
		if(result.equals(BlacklistVO.ABNORMAL)) {
			
			// [********** host list에서 제거 **********]

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {

	    				for(int s=0; s<hosts.length; s++) {
	        				String tUrl = StringUtil.nvl(hosts[s]).trim();
	    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
	    						if(tUrl.indexOf(verifiHost)>-1) {
	    							targetHostStr = tUrl;
	    						} else {
	    							hostList.add(tUrl);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** blacklist에 등록 **********]
	    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";

	    	file = new File(sroot);
	    	if(!file.exists()) {
	    		try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
        	if(!targetHostStr.equals("")) {
            	String str = FileUtil.roadLocalFile(sroot);
    			try {
    				FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
        	}
		}
		
		System.out.println("요청 받음 결과 result : "+result);
		if(result.equals(BlacklistVO.NORMAL) || result.equals(BlacklistVO.ABNORMAL)) {
			// [********** Seed server에 noti **********]
			String tempHosts = "";
			try {
				tempHosts = ApiHelper.postJSON(""+SEED_HOST+"/notiBlacklist", "{\"serviceId\":\""+serviceId+"\",\"remoteHost\":\""+localServiceHost+"\",\"initialClaimant\":\""+remoteAddessIp+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+result+"\"}");
				System.out.println("요청 받음 결과 result tempHosts : "+tempHosts);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return returnMsg;
	}

	/*
	 * blacklist 점검 결과 수정 
	 * seed server가 판단한 결과를 정정 처리.
	 * 비정상 sapp을 정상이라고 noti한경우 seed server에서 판단 후 정정 요청 처리를 한다. 점검 대상 sapp을 black list 처리 하고 host list 에서 제거 한다.
	 */
	@RequestMapping("/host/requestAbnormal")
	@ResponseBody
    public String requestAbnormal(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String returnMsg = "{ \"result\":\"OK\" }";

    	String serviceId = StringUtil.nvl(map.get("serviceId"));	// service id
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String verifiResult = StringUtil.nvl(map.get("verifiResult"));	// 검증 결과
    	if(serviceId.equals("")) return "{ \"result\":\"FAIL\" }";

		String result = BlacklistVO.getCheckResult(serviceId, verifiHost, verifiResult);
		if(result.equals(BlacklistVO.NORMAL)) {	// 정상으로 판단했었다면.
			
	    	// [********** get host list **********]
			String targetHostStr = "";
	    	List hostList = new ArrayList();
			String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {

	    				for(int s=0; s<hosts.length; s++) {
	        				String tUrl = StringUtil.nvl(hosts[s]).trim();
	    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
	    						if(tUrl.indexOf(verifiHost)>-1) {
	    							targetHostStr = tUrl;
	    						} else {
	    							hostList.add(tUrl);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}

	    	// [********** host list에서 제거 **********]
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** blacklist에 등록 **********]
	    	if(!targetHostStr.equals("")) {
		    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";
	
		    	file = new File(sroot);
		    	if(!file.exists()) {
		    		try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	        	String str = FileUtil.roadLocalFile(sroot);
				try {
					FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
				} catch (Exception e) {
					e.printStackTrace();
				}	
	    	}
		}
		
		return returnMsg;
	}

	/*
	 * blacklist 점검 결과 수정 
	 * seed server가 판단한 결과를 정정 처리.
	 * 정상 sapp을 비정상으로 신고된 경우 최초 신고 sapp을 blacklist 처리 하고 점검 sapp은 host list에 복원 시킨다.
	 * 만약 최초 검증대상으로 지목됐던 sapp이면 검증 결과를 가지고 있지 않기 때문에 seed server에서 확인하고 처리한다.
	 */
	@RequestMapping("/host/requestNormal")
	@ResponseBody
    public String requestNormal(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String returnMsg = "{ \"result\":\"OK\" }";

    	String serviceId = StringUtil.nvl(map.get("serviceId"));	// service id
    	String initialClaimant = StringUtil.nvl(map.get("initialClaimant"));	// blacklist 최초 신고 sapp host
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String verifiResult = StringUtil.nvl(map.get("verifiResult"));	// 검증 결과
    	if(serviceId.equals("")) return "{ \"result\":\"FAIL\" }";

    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

		String result = BlacklistVO.getCheckResult(serviceId, verifiHost, verifiResult);
		if(result.equals(BlacklistVO.ABNORMAL)) {	// 비정상으로 판단했었다면.

	    	// [********** get block host list **********]
			String targetHostStr = "";	// 최초 신고자 host
			String blockHostStr = "";	// 차단 했던 host
	    	List hostList = new ArrayList();
	    	List blockHostList = new ArrayList();
			String sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

	    	File file = new File(sroot);
	    	if(!file.exists()) {
	    		try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
	    	}
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {

	    				for(int s=0; s<hosts.length; s++) {
	        				String tUrl = StringUtil.nvl(hosts[s]).trim();
	    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
	    						if(tUrl.indexOf(verifiHost)>-1) {
	    							blockHostStr = tUrl;
	    						} else {
	    							blockHostList.add(tUrl);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	file = null;
	    	
	    	// [********** get host list **********]
	    	sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

	    	file = new File(sroot);
	    	if(!file.exists()) {
	    		try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
	    	}
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {

	    				for(int s=0; s<hosts.length; s++) {
	        				String tUrl = StringUtil.nvl(hosts[s]).trim();
	    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
	    						if(tUrl.indexOf(initialClaimant)>-1) {
	    							targetHostStr = tUrl;
	    						} else {
	    							hostList.add(tUrl);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    	
	    	// [********** 차단햇던 host는 block host list에서 제거 / 최초 신고자 host 추가 **********]
			sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");
			String writeContent = targetHostStr + "\n";
			try {
				for(int i=0; i<blockHostList.size(); i++) {
					writeContent += StringUtil.nvl(blockHostList.get(i),"")+"\n";
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** 최초 신고자 host list에서 제거 / 차단했던 검증 host는 다시 추가 **********]
	    	sroot = hostPropertiesPath+"host_"+serviceId+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");
			writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"\n" + blockHostStr + "\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}
				
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
		} else {	// 점검한 결과가 없으면

			String tempHosts = "";
			try {
				tempHosts = ApiHelper.postJSON(""+SEED_HOST+"/blacklistHostList", "{\"serviceId\":\""+serviceId+"\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(tempHosts.indexOf(initialClaimant)>-1) {	// 받은 blacklist에서 최초 검증요청자 host가 존재 할경우
				List hostList = new ArrayList();
				String targetHostStr = "";	// 최초 신고자 host
				
		    	// [********** get host list **********]
		    	String sroot = hostPropertiesPath+"host_"+serviceId+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");

		    	File file = new File(sroot);
		    	if(!file.exists()) {
		    		try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
		    	}
		    	if(file.exists()) {
		        	String str = FileUtil.roadLocalFile(sroot);
		        	String[] hosts = str.split("\n");				// data list
		        	
		    		if(str!=null && str.length()>0) {
		    			if(hosts.length>0) {

		    				for(int s=0; s<hosts.length; s++) {
		        				String tUrl = StringUtil.nvl(hosts[s]).trim();
		    					if(!tUrl.equals("") && !(StringUtil.nvl(hosts[s],"")).substring(0, 1).equals("#")) {
		    						if(tUrl.indexOf(initialClaimant)>-1) {
		    							targetHostStr = tUrl;
		    						} else {
		    							hostList.add(tUrl);
		    						}
		    					}
		    				}
		    			}
		    		}
		    	}
		    	file = null;
		    	
		    	// [********** 최초 신고자 blacklist 추가 **********]
		    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceId+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");

		    	file = new File(sroot);
		    	if(!file.exists()) {
		    		try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
		    	}
		    	if(file.exists()) {
		        	String str = FileUtil.roadLocalFile(sroot);
					try {
						FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
					} catch (Exception e) {
						returnMsg = "{ \"result\":\"FAIL\" }";
					}
		    	}
		    	file = null;
		    	
		    	// [********** 최초 신고자 host list에서 제거 / 차단했던 검증 host는 다시 추가 **********]
		    	sroot = hostPropertiesPath+"host_"+serviceId+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");
				String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss") + "\n";
				try {
					for(int i=0; i<hostList.size(); i++) {
						writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
					}
					
					FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				} catch (ParseException e) {
					returnMsg = "{ \"result\":\"FAIL\" }";
				} catch (Exception e) {
					returnMsg = "{ \"result\":\"FAIL\" }";
				}
		    	file = null;
			}
		}
    	
		
		return returnMsg;
	}
}
