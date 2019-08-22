<%@ page language="java" contentType="text/html; charset=UTF-8"     pageEncoding="UTF-8"%>
<%@ page import="java.io.BufferedReader"%>  
<%@ page import="java.io.IOException"%>  
<%@ page import="io.nodehome.svm.common.biz.ApiHelper"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
 
<%!
String readRequest(BufferedReader brReq) throws IOException {
	StringBuffer sbBuffer = new StringBuffer();
	String sLine;
	while ((sLine = brReq.readLine()) != null) {
		sbBuffer.append(sLine);		
	}	
	return sbBuffer.toString();
}
%>
<%
try {
	out.clear();
	String sBody = readRequest(request.getReader());
	String urlNodeM = "http://127.0.0.1:"+GlobalProperties.getProperty("nodem_port")+"/nodem.bin";
	String strRespone = ApiHelper.postJSON(urlNodeM,sBody);
	out.write(strRespone);
} catch (IOException e1) {
	e1.printStackTrace();
	out.write("{\"ec\":5 ,\"Pid\":\"pid\",\"value\":\"{}\",\"ref\":\"Unknown response\"}");
}
%>
