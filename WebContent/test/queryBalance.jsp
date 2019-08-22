<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%> 
<%@ page import="io.nodehome.svm.common.util.EtcUtils"%>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.biz.ApiHelper"%>
<%@ page import="io.nodehome.svm.common.util.KeyManager"%>
<%@ page import="net.fouri.libs.bitutil.crypto.InMemoryPrivateKey"%>
<%@ page import="net.fouri.libs.bitutil.model.NetConfig"%>
<%@ page import="java.util.concurrent.TimeUnit"%>
<%@ page import="java.text.DecimalFormat"%>

<html>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script src="/js/cpwallet/common.js"></script>
<script>
function actionform() {
	pform.submit();
}
</script>
<body>
<%
HttpSession loginsession = request.getSession(true); // true : 없으면 세션 새로 만듦

request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

//-------------------------------------
String strRedirectResponse = null;
String strAccountID = StringUtil.nvl(request.getParameter("aid"));
String strWID = StringUtil.nvl(request.getParameter("wid"));

int tryCnt = Integer.parseInt(StringUtil.nvl(request.getParameter("tryCnt"),"0"));
%>
<form action="./queryBalance.jsp" method="post" name="pform" id="pform" onsubmit="return false;">
	 재시도 횟수 <input name="tryCnt" id="tryCnt" class="form-control" type="text" placeholder="TITLE" value="<%=tryCnt%>" maxlength="50" />
	 <button type="button" class="btn btn-primary" onclick="actionform()">실행</button>
	 <br/>
	 <div id="cons" style="width:100%;height:500px;border:1px solid #ccc;overflow:scroll;" class="form-control"></div>
</form>

	<%
	DecimalFormat df = new DecimalFormat("##.##") ;
	if(tryCnt>0) {
		double timeSum = 0;
		for(int i=0; i<tryCnt; i++) {
				double startTime = 0;
				double endTime = 0;
				startTime = System.currentTimeMillis(); 

				String[] args2 = null;
				args2 = new String[] {"PID","10000","cMhRZxunbKX4Hb4zgWnvJw7cbnQ1GAsPCJhN5MFtEv3cNFhUzqVj","","",""};
				ApiHelper.putQuerySignature(args2,true,"query",ApiHelper.EC_CHAIN,KeyManager.getPrivateKey(KeyManager.CPAPI_LEVEL.emMan300));
				JSONObject joRes_temp = CPWalletUtil.getValue(ApiHelper.EC_CHAIN,"queryBalance", "",args2);
				
				endTime = System.currentTimeMillis();
				double term = (endTime/1000) - (startTime/1000);
				%>
				<script>$('#cons').prepend("<%=df.format(term)+" 초<br/>" %>");</script>
				<%				
				timeSum+=term;
		} %>
		
		전체 경과 시간 : <%=df.format(timeSum)+" 초" %><br/>
		평균 시간 : <%=df.format((timeSum/tryCnt))+" 초" %><br/>
	<% } %>
</body>
</html>