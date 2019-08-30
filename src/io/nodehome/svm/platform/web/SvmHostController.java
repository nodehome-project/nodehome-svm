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
import io.nodehome.svm.common.util.SigToolUtil;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class SvmHostController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvmHostController.class);

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
		
		// 시작 시간
        long sa_startTime = System.currentTimeMillis();
        
		String returnMsg = "";
    	String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
    	String strFail = "{ \"result\":\"FAIL\", \"apiVersion\":\""+GlobalProperties.getProperty("Globals.apiVersion")+"\" }";
    	String strResultGubun = "ACCEPT";
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String seedHost = StringUtil.nvl(map.get("seedHost"),"");

    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

    	String sroot = hostPropertiesPath+"host-"+chainID+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	//System.out.println("sroot : " +sroot);

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
    				if(hosts[s]!=null && !hosts[s].equals("") && !(hosts[s].substring(0, 1)).equals("#") && !(hosts[s]).equals(seedHost)) {	// Comments or seed host addresses are excluded from the calculation.
    					localHostList[renewalIndex++] = StringUtil.nvl(hosts[s].toString());
    				}
    			}
    			
        		// If it has been more than one month since the update, / file list update processing - START
            	String fileDate = hosts[0].toString().replaceAll("# ","");
            	String[] hHader = fileDate.split(Pattern.quote("|"));
            	String preMonth = DateUtil.getPlusMinusDate(DateUtil.getDate("yyyyMMddHHmmss"), -30, "yyyyMMddHHmmss");	// Renew files once a month
            	if(Double.parseDouble(hHader[0]) < Double.parseDouble(preMonth)) {

            		String tempHosts = urlConnection(seedHost+"/seedhost","{\"serviceID\":\""+serviceID+"\", \"chainID\":\""+chainID+"\"}");

            		if(!tempHosts.equals("")) {

                		JSONParser paRes = new JSONParser();
    					JSONObject joRes = (JSONObject) paRes.parse(tempHosts);
    					String result = (String)joRes.get("result");

    					if(result.equals("ACCEPT") || result.equals("LIST")) {
    						JSONArray hostArrary = (JSONArray) paRes.parse(joRes.get("list").toString()) ;
    						if(hostArrary!=null && hostArrary.size()>0) {
	    						for(int i=0; i<hostArrary.size(); i++) {
        	    					if(!(StringUtil.nvl(hostArrary.get(i),"")).substring(0, 1).equals("#")) {
        	    						updateHostList.add(StringUtil.nvl(hostArrary.get(i),""));
        	    					}
	    						}
        					}
        					paRes = null;
    					}
            		}
        			
        			if(updateHostList!=null) {
        				hostList = new String[updateHostList.size()];
        				for(int i=0; i<updateHostList.size(); i++) {
        					hostList[i] = updateHostList.get(i);
        				}
        			}

        			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+hHader[1]+"|"+hHader[2]+"\n";
					for(int i=0; i<hostList.length; i++) {
						String tUrl = StringUtil.nvl(hostList[i]).trim();
						if(!(tUrl).equals(""))
							writeContent += StringUtil.nvl(hostList[i],"")+"\n";
					}
					System.out.println("15 sroot : " +sroot);
					FileUtil.writeFile(sroot, "utf-8", false, writeContent);
					nodemReloadHost();
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
    				
    				// 종료 시간
    		        long sa_endTime = System.currentTimeMillis();
    		        
    		        // 경과시간
    		        long sa_elapsedTime = sa_endTime - sa_startTime;
    		        
    		        // result
    		        //System.out.println("sa_startTime : " + sa_startTime);
    		        //System.out.println("sa_endTime : " + sa_endTime);
    		        //System.out.println("sa_elapsedTime : " + sa_elapsedTime);
    		        
    				returnMsg = "{ \"result\":\""+strResultGubun+"\",\"list\":["+returnUrl+"],\"sa_startTime\":"+sa_startTime+",\"sa_endTime\":"+sa_endTime+",\"sa_elapsedTime\":"+sa_elapsedTime+", \"apiVersion\":\""+GlobalProperties.getProperty("Globals.apiVersion")+"\" }";
    				
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

    	System.out.println("returnMsg : " + returnMsg.toString());
    	return returnMsg.toString();
    }

	/*
	 * Simple lookup
	 */
	@RequestMapping("/host/getSimpleHostList")
	@ResponseBody
	public String getSimpleHostList(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) throws UnknownHostException, IOException {
    	String returnMsg = "";
    	String serviceID = StringUtil.nvl(map.get("serviceID"),GlobalProperties.getProperty("project_serviceID"));
    	String strFail = "{ \"result\":\"FAIL\" }";
    	String strResultGubun = "ACCEPT";
		String chainID = StringUtil.nvl(map.get("chainID"),"");

    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

    	String sroot = hostPropertiesPath+"host-"+chainID+".properties";
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
    	String serviceID = StringUtil.nvl(map.get("serviceID"), GlobalProperties.getProperty("project_serviceID"));
    	String requestUrl = StringUtil.nvl(map.get("requestUrl"));
    	String targetUrl = StringUtil.nvl(map.get("targetUrl"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
    	if(requestUrl.equals("")) getResultJsonMessage("FAIL", "empty url");
    	System.out.println("requestVerification targetUrl : "+targetUrl);
    	
    	String returnMsg = checkBlacklist(request, requestUrl, serviceID, targetUrl, mport);
    	if(returnMsg.indexOf(BlacklistVO.ABNORMAL)>-1) {
    		result = "FAIL";
    		message = "Fake host.";
    	} else if(returnMsg.indexOf(BlacklistVO.NORMAL)>-1) {
    		result = "OK";
    		message = "Normal host.";
    	}
    	
		return getResultJsonMessage(result, message);
	}

	@RequestMapping(value = "/checkNode", produces = "application/json; charset=utf8")
	@ResponseBody
    public String checkNode(HttpServletRequest request) {
		long startTime = System.currentTimeMillis();
		
		String strRespone = "";
		try {
			// 검사 실행
			//out.clear();
			String sBody = readRequest(request.getReader());
			String urlNodeM = "http://127.0.0.1:" + GlobalProperties.getProperty("nodem_port") + "/nodem.bin";
			strRespone = ApiHelper.postJSON(urlNodeM, sBody);
			if(sBody.indexOf("REQ_SNS_checknode")>-1) {
				long endTime = System.currentTimeMillis();
				long elapsedTime = endTime - startTime;
				JSONObject nodemRes = null;
				JSONParser parser = new JSONParser();
				try {
					nodemRes = (JSONObject)parser.parse(strRespone);
					nodemRes.put("sa_startTime", startTime);
					nodemRes.put("sa_endTime", endTime);
					nodemRes.put("sa_elapsedTime", elapsedTime);
					strRespone = nodemRes.toJSONString();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
			strRespone = "{\"ec\":5 ,\"Pid\":\"pid\",\"value\":{},\"ref\":\"Unknown response\"}";
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
	
    public String checkBlacklist(HttpServletRequest request, String localServiceHost, String serviceID, String verifiHost, String mport) {
		String returnMsg = "{ \"result\":\"OK\" , \"message\":\""+BlacklistVO.NORMAL+"\" }";

    	String remoteAddessIp = request.getRemoteAddr();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

    	String sttime = DateUtil.getDate("yyyyMMddHHmmss");

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
    	
    	String tempHosts = "";
    	String verifiResult = "";
	
    	JSONObject nodemRes = null;
    	JSONParser parser = new JSONParser();

		String localServiceHost2 = localServiceHost;
		try {
			String protocol = localServiceHost2.substring(0,localServiceHost2.indexOf("://")+3);
			localServiceHost2 = localServiceHost2.substring(localServiceHost2.indexOf("://")+3);
			if(localServiceHost2.indexOf(":")>-1)
				localServiceHost2 = localServiceHost2.substring(0,localServiceHost2.indexOf(":")-1);
			
			System.out.println("nodem check ready ");
			tempHosts = ApiHelper.postJSON(protocol+localServiceHost2+":"+GlobalProperties.getProperty("nodem_port")+"/nodem.bin", "{ \"cmd\":\"REQ_SN_checknode\" , \"pid\":\"pid\",\"ver\":10000, \"args\":[\""+localServiceHost+"\" ,\""+serviceID+"\", \""+verifiHost+"\" ,\""+mport+"\"] }");
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
		System.out.println("검증 결과 verifiResult serviceID : "+serviceID);
		BlacklistVO.setCheckResult(serviceID, remoteAddessIp, verifiHost, verifiResult);
		
		System.out.println("검증 결과 verifiResult : "+verifiResult);
		if(verifiResult.equals(BlacklistVO.ABNORMAL)) {

			File dirFile = new File(hostPropertiesPath);
			File[] fileList = dirFile.listFiles();
			for (File tempFile : fileList) {
				List hostList = new ArrayList();
				if (tempFile.isFile()) {
					String tempPath = tempFile.getParent();
					String tempFileName = tempFile.getName();
					
					
					
					

			    	if(tempFileName.substring(0,5).equals("host-")) {

						// System.out.println("verifiResult : " +verifiResult);
						String targetHostStr = "";
						String sroot = hostPropertiesPath + tempFileName;
				    	sroot = sroot.replaceAll("\\\\", "/");
				    	String seedHost = "";
				    	String pubk = "";

				    	File file = new File(sroot);
				    	if(file.exists()) {
				        	String str = FileUtil.roadLocalFile(sroot);
				        	String[] hosts = str.split("\n");
				        	if(hosts.length>1 && hosts[0].substring(0, 1).equals("#")) {
				        		String[] headerArr = hosts[0].split(Pattern.quote("|"));
				        		pubk = headerArr[1];
					        	seedHost = headerArr[2];
				        	}
				        	
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
				    	
				    	// hostlist 에 문제 host가 존재할 경우
				    	if(!targetHostStr.equals("")) {
				    		String chainID = tempFileName.replaceAll("host-","").replaceAll(".properties","");

							// [ blacklist 파일 등록 ]
					    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";
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
				        	if(str.indexOf(targetHostStr)<0) {
								try {
									if(str.indexOf(targetHostStr)==-1)
										FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
								} catch (Exception e) {
									e.printStackTrace();
								}	
				        	}
							
							// [********** host list에서 제거 **********]
							sroot = hostPropertiesPath+"host-"+chainID+".properties";
					    	sroot = sroot.replaceAll("\\\\", "/");

							String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+pubk+"|"+seedHost+"\n";
							try {
								for(int i=0; i<hostList.size(); i++) {
									writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
								}

								FileUtil.writeFile(sroot, "utf-8", false, writeContent);
								nodemReloadHost();
							} catch (ParseException e) {
								returnMsg = "{ \"result\":\"FAIL\" }";
							} catch (Exception e) {
								returnMsg = "{ \"result\":\"FAIL\" }";
							}

							// [ Send noti to seed host ]
							tempHosts = "";
							try {
								tempHosts = ApiHelper.postJSON(""+seedHost+"/notiBlacklist", "{\"sttime\":\""+sttime+"\", \"serviceID\":\""+serviceID+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+verifiResult+"\",\"chainID\":\""+chainID+"\",\"seedHost\":\""+seedHost+"\"}");
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							
							// [ broadcasting ]
							sroot = hostPropertiesPath+"host-"+chainID+".properties";
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
						        					//System.out.println(tUrl[0] + "에 검증 요청!");
						        					tempHosts = ApiHelper.postJSON(""+tUrl[0]+"/host/requestBlockHost", "{\"seedHost\":\""+seedHost+"\",\"sttime\":\""+sttime+"\",\"reporterHost\":\""+localServiceHost+"\",\"serviceID\":\""+serviceID+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+verifiResult+"\",\"mport\":\""+mport+"\",\"chainID\":\""+chainID+"\"}");
						        					//System.out.println(tUrl[0] + "에 검증 결과 : "+tempHosts);
						        				} catch (IOException e1) {
						        					//e1.printStackTrace();
						        				}
					    					}
					    				}
					    			}
					    		}
					    	}
				    	} // if(!targetHostStr.equals(""))
			    	} // if(tempFileName.substring(0,5).equals("host-"))
					
					
					
					
				}
			}
	    	
		} 
		
		return returnMsg;
	}
    
    

    // 검사만 하고 broadcasting 없음
    public String checkBlacklist2(HttpServletRequest request, String serviceID, String verifiHost, String mport) {
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
			
			tempHosts = ApiHelper.postJSON(protocol+localServiceHost2+":"+GlobalProperties.getProperty("nodem_port")+"/nodem.bin", "{ \"cmd\":\"REQ_SN_checknode\" , \"pid\":\"pid\",\"ver\":10000, \"args\":[\""+localServiceHost+"\" ,\""+serviceID+"\", \""+verifiHost+"\" ,\""+mport+"\"] }");
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
				returnMsg = "{ \"result\":\""+verifiResult+"\" , \"message\":\"Verification OK\" }";
			} else if(nCode==1 || nCode==5 || nCode==7 || nCode==8 || nCode==19) {
				verifiResult = BlacklistVO.ABNORMAL;
				returnMsg = "{ \"result\":\""+verifiResult+"\" , \"message\":\"Hacked Sources\" }";
			} else {
				returnMsg = "{ \"result\":\"FAIL\" , \"message\":\"Verification Fail\" }";
			}
		}
		/*
		 ********************************* node manager 검증 프로세스 끝
		 */
		
		// [ 검사 결과 메모리 저장 ]
		BlacklistVO.setCheckResult(serviceID, remoteAddessIp, verifiHost, verifiResult);
		
		return returnMsg;
	}
    
	/*
	 * 브로드케스팅 1단계
	 * blacklist 등록 요청 전파 받음
	 * 최초 신고SApp이 다른 Sapp 에 blacklist요청 할떄 호출. 신고 받은 내용 검증 후 blacklist 처리하고 seed server에 notification.
	 */
	@RequestMapping("/host/requestBlockHost")
	@ResponseBody
    public String requestBlockHost(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String returnMsg = "{ \"result\":\"OK\" }";
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
		String seedHost = StringUtil.nvl(map.get("seedHost"),"");
    	//System.out.println("요청 받음 /host/requestBlockHost - localServiceHost : "+localServiceHost);

    	String remoteAddessIp = request.getRemoteAddr();	
    	String reporterHost = StringUtil.nvl(map.get("reporterHost")); // 최초 요청자 host
    	
    	String serviceID = StringUtil.nvl(map.get("serviceID"));	// service id
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String mport = StringUtil.nvl(map.get("mport"));
    	String sttime = StringUtil.nvl(map.get("sttime"));
		String chainID = StringUtil.nvl(map.get("chainID"),"");
    	
    	if(serviceID.equals("")) return "{ \"result\":\"FAIL\" }";

		// [********** 검증 **********]
		/*
		 * node manager 검증 프로세스 시작
		 */
		String result = ""; // normal / abnormal 판정
    	JSONParser paRes = new JSONParser();
		returnMsg = checkBlacklist2(request, serviceID, verifiHost, mport);	// Verification 
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
			BlacklistVO.setCheckResult(serviceID, remoteAddessIp, verifiHost, result);
		
		// [********** 검증결과가 비정상일떄만 blacklist에 등록 **********]
		String sroot = "";
		String targetHostStr = "";
    	List hostList = new ArrayList();
		sroot = hostPropertiesPath+"host-"+chainID+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	String pubk = "";
    	
		if(result.equals(BlacklistVO.ABNORMAL)) {
			
			// [********** host list에서 제거 **********]

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {
			        	if(hosts.length>1 && hosts[0].substring(0, 1).equals("#")) {
			        		String[] headerArr = hosts[0].split(Pattern.quote("|"));
			        		pubk = headerArr[1];
			        	}

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
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+pubk+"|"+seedHost+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}

				System.out.println("3 sroot : " +sroot);
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				nodemReloadHost();
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** blacklist에 등록 **********]
	    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";

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
    				if(str.indexOf(targetHostStr)==-1)
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
				tempHosts = ApiHelper.postJSON(""+seedHost+"/notiBlacklist", "{\"sttime\":\""+sttime+"\", \"serviceID\":\""+serviceID+"\",\"remoteHost\":\""+localServiceHost+"\",\"initialClaimant\":\""+remoteAddessIp+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+result+"\",\"chainID\":\""+chainID+"\",\"seedHost\":\""+seedHost+"\"}");
				System.out.println("요청 받음 결과 result tempHosts : "+tempHosts);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		
		if(result.equals(BlacklistVO.NORMAL)) {
			// 최초 요청자 : reporterHost

			// [********** 검증 **********]
			/*
			 * node manager 검증 프로세스 시작
			 */
			result = ""; // normal / abnormal 판정
	    	paRes = new JSONParser();
			returnMsg = checkBlacklist2(request, serviceID, reporterHost, mport);	// Verification 
			try {
				JSONObject joRes = (JSONObject) paRes.parse(returnMsg);
				result = String.valueOf(joRes.get("result"));
				//System.out.println(localServiceHost+ "2차 검사요청받은 sapp 검사결과 : "+result);
			} catch (ParseException e2) {
				e2.printStackTrace();
			}

			if(result.equals(BlacklistVO.ABNORMAL)) {
				// [********** Seed server에 noti **********]
				String tempHosts = "";
				try {
					tempHosts = ApiHelper.postJSON(""+seedHost+"/notiBlacklist", "{\"sttime\":\""+sttime+"\", \"serviceID\":\""+serviceID+"\",\"remoteHost\":\""+localServiceHost+"\",\"initialClaimant\":\""+remoteAddessIp+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+result+"\",\"chainID\":\""+chainID+"\",\"seedHost\":\""+seedHost+"\"}");
					//System.out.println("요청 받음 결과 result tempHosts : "+tempHosts);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// [ broadcasting 2 ]
				sroot = hostPropertiesPath+"host-"+chainID+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");
		    	
		    	File file = new File(sroot);
		    	if(file.exists()) {
		    		String str = FileUtil.roadLocalFile(sroot);
		        	String[] hosts = str.split("\n");
		        	
		    		if(str!=null && str.length()>0) {
		    			if(hosts.length>0) {
		    				for(int s=0; s<hosts.length; s++) {
		    					String hUrl = StringUtil.nvl(hosts[s],"");
		    					if(!hUrl.equals("") && !hUrl.substring(0, 1).equals("#") && hUrl.indexOf(reporterHost)<0 && hUrl.indexOf(localServiceHost)<0) {
			        				String[] tUrl = hUrl.split(Pattern.quote("|"));
			        				tempHosts = "";
			        				try {
			        					tempHosts = ApiHelper.postJSON(""+tUrl[0]+"/host/requestBlockHostStep2", "{\"sttime\":\""+sttime+"\", \"reporterHost\":\""+localServiceHost+"\",\"serviceID\":\""+serviceID+"\",\"verifiHost\":\""+reporterHost+"\",\"verifiResult\":\""+result+"\",\"mport\":\""+mport+"\",\"chainID\":\""+chainID+"\"}");
			        				} catch (IOException e1) {
			        				}
		    					}
		    				}
		    			}
		    		}
		    	}
			}
			
		}
		
		
		return returnMsg;
	}

    
	/*
	 * 브로드케스팅 2단계
	 * blacklist 등록 요청 전파 받음
	 * 최초 신고SApp이 다른 Sapp 에 blacklist요청 할떄 호출. 신고 받은 내용 검증 후 blacklist 처리하고 seed server에 notification.
	 */
	@RequestMapping("/host/requestBlockHostStep2")
	@ResponseBody
    public String requestBlockHostStep2(HttpServletRequest request, @RequestBody HashMap<String,String> map, ModelMap model) {
		String returnMsg = "{ \"result\":\"OK\" }";
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
		String seedHost = StringUtil.nvl(map.get("seedHost"),"");
    	//System.out.println("요청 받음 /host/requestBlockHost - localServiceHost : "+localServiceHost);

    	String remoteAddessIp = request.getRemoteAddr();	
    	String reporterHost = StringUtil.nvl(map.get("reporterHost")); // 최초 요청자 host
		String chainID = StringUtil.nvl(map.get("chainID"),"");
    	
    	String serviceID = StringUtil.nvl(map.get("serviceID"));	// service id
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String mport = StringUtil.nvl(map.get("mport"));
    	String sttime = StringUtil.nvl(map.get("sttime"));
    	
    	if(serviceID.equals("")) return "{ \"result\":\"FAIL\" }";

		// [********** 검증 **********]
		/*
		 * node manager 검증 프로세스 시작
		 */
		String result = ""; // normal / abnormal 판정
    	JSONParser paRes = new JSONParser();
		returnMsg = checkBlacklist2(request, serviceID, verifiHost, mport);	// Verification 
		try {
			JSONObject joRes = (JSONObject) paRes.parse(returnMsg);
			result = String.valueOf(joRes.get("result"));
			//System.out.println(localServiceHost+ " 검사요청받은 sapp 검사결과 : "+result);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		/*
		 * node manager 검증 프로세스 끝
		 */
		
		// [********** 검사 결과 메모리 저장 **********]
		if(result.equals(BlacklistVO.NORMAL) || result.equals(BlacklistVO.ABNORMAL))
			BlacklistVO.setCheckResult(serviceID, remoteAddessIp, verifiHost, result);
		
		// [********** 검증결과가 비정상일떄만 blacklist에 등록 **********]
		String sroot = "";
		String targetHostStr = "";
    	List hostList = new ArrayList();
		sroot = hostPropertiesPath+"host-"+chainID+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	String pubk = "";
    	
		if(result.equals(BlacklistVO.ABNORMAL)) {

			// [********** host list에서 제거 **********]

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {
			        	if(hosts.length>1 && hosts[0].substring(0, 1).equals("#")) {
			        		String[] headerArr = hosts[0].split(Pattern.quote("|"));
			        		pubk = headerArr[1];
			        	}

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
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+pubk+"|"+seedHost+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}

				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				nodemReloadHost();
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** blacklist에 등록 **********]
	    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";

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
    				if(str.indexOf(targetHostStr)==-1)
    					FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
        	}
		}
		
		if(result.equals(BlacklistVO.NORMAL) || result.equals(BlacklistVO.ABNORMAL)) {
			// [********** Seed server에 noti **********]
			String tempHosts = "";
			try {
				tempHosts = ApiHelper.postJSON(""+seedHost+"/notiBlacklist", "{\"sttime\":\""+sttime+"\", \"serviceID\":\""+serviceID+"\",\"remoteHost\":\""+localServiceHost+"\",\"initialClaimant\":\""+remoteAddessIp+"\",\"verifiHost\":\""+verifiHost+"\",\"verifiResult\":\""+result+"\",\"chainID\":\""+chainID+"\",\"seedHost\":\""+seedHost+"\"}");
				//System.out.println("요청 받음 결과 result tempHosts : "+tempHosts);
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

    	String serviceID = StringUtil.nvl(map.get("serviceID"));	// service id
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String verifiResult = StringUtil.nvl(map.get("verifiResult"));	// 검증 결과
    	if(serviceID.equals("")) return "{ \"result\":\"FAIL\" }";
		String chainID = StringUtil.nvl(map.get("chainID"),"");

		String result = BlacklistVO.getCheckResult(serviceID, verifiHost, verifiResult);
		if(result.equals(BlacklistVO.NORMAL)) {	// 정상으로 판단했었다면.
			String pubk = "";
			String seedHost= "";
			
	    	// [********** get host list **********]
			String targetHostStr = "";
	    	List hostList = new ArrayList();
			String sroot = hostPropertiesPath+"host-"+chainID+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");

	    	File file = new File(sroot);
	    	if(file.exists()) {
	        	String str = FileUtil.roadLocalFile(sroot);
	        	String[] hosts = str.split("\n");				// data list
	        	
	    		if(str!=null && str.length()>0) {
	    			if(hosts.length>0) {
			        	if(hosts.length>1 && hosts[0].substring(0, 1).equals("#")) {
			        		String[] headerArr = hosts[0].split(Pattern.quote("|"));
			        		pubk = headerArr[1];
			        		seedHost = headerArr[2];
			        	}

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
			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+pubk+"|"+seedHost+"\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}

				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				nodemReloadHost();
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
	    	// [********** blacklist에 등록 **********]
	    	if(!targetHostStr.equals("")) {
		    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";
	
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
					if(str.indexOf(targetHostStr)==-1)
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

    	String serviceID = StringUtil.nvl(map.get("serviceID"));	// service id
    	String initialClaimant = StringUtil.nvl(map.get("initialClaimant"));	// blacklist 최초 신고 sapp host
    	String verifiHost = StringUtil.nvl(map.get("verifiHost"));	// blacklist 대상 sapp host
    	String verifiResult = StringUtil.nvl(map.get("verifiResult"));	// 검증 결과
    	if(serviceID.equals("")) return "{ \"result\":\"FAIL\" }";
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String seedHost = StringUtil.nvl(map.get("seedHost"),"");
		
    	String localServiceHost = request.getRequestURL().toString();
    	localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");

		String result = BlacklistVO.getCheckResult(serviceID, verifiHost, verifiResult);
		if(result.equals(BlacklistVO.ABNORMAL)) {	// 비정상으로 판단했었다면.

	    	// [********** get block host list **********]
			String targetHostStr = "";	// 최초 신고자 host
			String blockHostStr = "";	// 차단 했던 host
	    	List hostList = new ArrayList();
	    	List blockHostList = new ArrayList();
			String sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";
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

	    	String pubk = "";
	    	
	    	// [********** get host list **********]
	    	sroot = hostPropertiesPath+"host-"+chainID+".properties";
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
			        	if(hosts.length>1 && hosts[0].substring(0, 1).equals("#")) {
			        		String[] headerArr = hosts[0].split(Pattern.quote("|"));
			        		pubk = headerArr[1];
			        	}

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
			sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";
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
	    	sroot = hostPropertiesPath+"host-"+chainID+".properties";
	    	sroot = sroot.replaceAll("\\\\", "/");
			writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+pubk+"|"+seedHost+"\n" + blockHostStr + "\n";
			try {
				for(int i=0; i<hostList.size(); i++) {
					writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
				}

				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				nodemReloadHost();
			} catch (ParseException e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			} catch (Exception e) {
				returnMsg = "{ \"result\":\"FAIL\" }";
			}
	    	file = null;
	    	
		} else {	// 점검한 결과가 없으면

			String tempHosts = "";
			try {
				tempHosts = ApiHelper.postJSON(""+seedHost+"/blacklistHostList", "{\"serviceID\":\""+serviceID+"\",\"chainID\":\""+chainID+"\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(tempHosts.indexOf(initialClaimant)>-1) {	// 받은 blacklist에서 최초 검증요청자 host가 존재 할경우
				List hostList = new ArrayList();
				String targetHostStr = "";	// 최초 신고자 host
				String headerLine = "";
				String pubk = "";
				
		    	// [********** get host list **********]
		    	String sroot = hostPropertiesPath+"host-"+chainID+".properties";
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
		    				if(hosts[0].indexOf("#")>-1) {
		    					pubk = hosts[0].split(Pattern.quote("|"))[1];
		    				}

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
		    	sroot = hostPropertiesPath.replaceAll("\\\\", "/") + "blacklist_"+serviceID+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");

		    	file = new File(sroot);
		    	if(!file.exists()) {
		    		try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
		    	}
		    	if(file.exists()) {
		        	String str = FileUtil.roadLocalFile(sroot);
					try {
						if(str.indexOf(targetHostStr)==-1)
	    					FileUtil.writeFile(sroot, "utf-8", false, targetHostStr+"\n"+str);
					} catch (Exception e) {
						returnMsg = "{ \"result\":\"FAIL\" }";
					}
		    	}
		    	file = null;
		    	
		    	// [********** 최초 신고자 host list에서 제거 / 차단했던 검증 host는 다시 추가 **********]
		    	sroot = hostPropertiesPath+"host-"+chainID+".properties";
		    	sroot = sroot.replaceAll("\\\\", "/");
				String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss") + "|"+pubk+"|"+seedHost+"\n";
				try {
					for(int i=0; i<hostList.size(); i++) {
						writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
					}

					FileUtil.writeFile(sroot, "utf-8", false, writeContent);
					nodemReloadHost();
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

	/*
	 * SApp 버전 문제로 hostlist 에서 제거 요청 처리
	 */
	@RequestMapping("/requestRemoveHost")
	@ResponseBody
    public String requestRemoveHost(HttpServletRequest request, @RequestBody HashMap<String,String> map) {
		String returnMsg = "{ \"result\":\"OK\" }";
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		
    	String serviceID = StringUtil.nvl(map.get("serviceID"));				// service id
    	String requestHost = StringUtil.nvl(map.get("requestHost"));
    	
    	if(serviceID.equals("")) return "{ \"result\":\"FAIL\" }";
		
    	String sroot = hostPropertiesPath+"host-"+chainID+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	
    	// [ load hostList ]
    	List hostList = new ArrayList();
    	File file = new File(sroot);

    	String hostFileHeader = "";
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	str = str.substring(0,str.length()-1);
        	String[] hosts = str.split("\n");
			if(hosts!=null && !StringUtil.nvl(hosts[0]).equals("") && hosts.length>0 && (hosts[0]).substring(0, 1).equals("#")) {
				for(int s=0; s<hosts.length; s++) {
					if((hosts[s]).substring(0, 1).equals("#")) {
						hostFileHeader=hosts[s].toString();
					}
					if(!StringUtil.nvl(hosts[s]).equals("") && hosts[s].length()>0 && !(hosts[s]).substring(0, 1).equals("#")) {
    					hostList.add(hosts[s].toString());
					}
				}
			}
    	}

		String writeContent = hostFileHeader+"\n";
    	if(hostList!=null) {
			for(int i=0; i<hostList.size(); i++) {
				String row = hostList.get(i).toString();
	        	if(row.indexOf(requestHost)==-1) {
	        		writeContent += StringUtil.nvl(hostList.get(i),"")+"\n";
	        	}
			}
			try {
				System.out.println("13 sroot : " +sroot);
				FileUtil.writeFile(sroot, "utf-8", false, writeContent);
				nodemReloadHost();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}

		return returnMsg;
	}
	
	//************************************************************ 체인 관리자 체인 hostlist 관리 시작
	@RequestMapping("/host/syncHostList")
	@ResponseBody
    public String syncHostList(HttpServletRequest request, @RequestBody HashMap<String,String> map) {
		String returnMsg = "{\"result\":\"FAIL\"}";
		String action = StringUtil.nvl(map.get("action"),"");
		String chainID = StringUtil.nvl(map.get("chainID"),"");
		String netType = StringUtil.nvl(map.get("netType"),"");
		String list = StringUtil.nvl(map.get("list"),"");
		String sig = StringUtil.nvl(map.get("sig"),"");
		String ndate = StringUtil.nvl(map.get("ndate"),"");
		String[] hostList = list.split(","); 

		long paramNDate = Long.parseLong(ndate);
		long sysNDate = (DateUtil.getTimeStamp())/1000;
		System.out.println("sysNDate - paramNDate : "+(paramNDate - sysNDate));
		
		if((paramNDate - sysNDate) < -100 || (paramNDate - sysNDate) > 100) {
			returnMsg = "{\"result\":\"FAIL\",\"ref\":\"The request time is incorrect.\"}";
			return returnMsg;
		}

//		System.out.println();
//		System.out.println("action : "+action);
//		System.out.println("chainID : "+chainID);
//		System.out.println("netType : "+netType);
//		System.out.println("list : "+list);
//		System.out.println("sig : "+sig);
		
    	String sroot = hostPropertiesPath+"host-"+chainID+".properties";
    	sroot = sroot.replaceAll("\\\\", "/");
    	String hHader = "";
    	String header_pubk = "";
    	String seed_host = "";

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	String[] hosts = str.split("\n");		

        	if((hosts[0]).indexOf("#")>-1) {
        		hHader = hosts[0].toString();
        	}
        	String[] t = hHader.split(Pattern.quote("|"));
        	if(t!=null && t.length>1) {
            	header_pubk = t[1];
            	seed_host = t[2];

            	if(header_pubk!=null && !header_pubk.equals("")) {
                	// signature 검증
            	    SigToolUtil sigToolUtil = new SigToolUtil(GlobalProperties.getProperty("pcwallet.path"));
            	    String verifyRes = "";
            		try {
        				verifyRes = sigToolUtil.getVerifySignature(chainID+list, header_pubk, sig, chainID);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
            		JSONParser parser = new JSONParser();
            		try {
						JSONObject jsonVerifyRes = (JSONObject)parser.parse(verifyRes);

	            		if(jsonVerifyRes!=null && jsonVerifyRes.get("Result")!=null && str!=null && str.length()>0) {
	            			if(((String)jsonVerifyRes.get("Result")).equals("OK")) {
		            			if(hostList!=null && hostList.length>0) {
		                			String writeContent = "# "+DateUtil.getDate("yyyyMMddHHmmss")+"|"+header_pubk+"|"+seed_host+"\n";
		        					for(int i=0; i<hostList.length; i++) {
		        						String tUrl = StringUtil.nvl(hostList[i]).trim();
		        						if(!(tUrl).equals(""))
		        							writeContent += StringUtil.nvl(hostList[i],"")+"\n";
		        					}
		        					try {
										FileUtil.writeFile(sroot, "utf-8", false, writeContent);
										nodemReloadHost();
									} catch (Exception e) {
										e.printStackTrace();
									}
		            				returnMsg = "{\"result\":\"OK\",\"ref\":\"Success\"}";
		            			}	
	            			}
	            		}
					} catch (ParseException e) {
						e.printStackTrace();
					}

            	}
        	}
    	}
    	
		return returnMsg;
	}
	//************************************************************ 체인 관리자 체인 hostlist 관리 끝
	
	// nodem host list reload
	private String nodemReloadHost() {
		String r = "";
		try {
			r = ApiHelper.postJSON("http://127.0.0.1:"+GlobalProperties.getProperty("nodem_port")+"/reloadhost.bin", "{\"url\":\""+GlobalProperties.getProperty("Globals.serviceHost")+"\"}");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return r;
	}
}
