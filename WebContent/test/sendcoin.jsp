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
<form action="./sendcoin.jsp" method="post" name="pform" id="pform" onsubmit="return false;">
	 재시도 횟수 <input name="tryCnt" id="tryCnt" class="form-control" type="text" placeholder="TITLE" value="<%=tryCnt%>" maxlength="50" />
	 <button type="button" class="btn btn-primary" onclick="actionform()">실행</button>
	 <br/>
	 <div id="cons" style="width:100%;height:500px;border:1px solid #ccc;" class="form-control"></div>
</form>

	<%
	// reciver 
	// cMg7JyLRn3MGxiWcv9bv66dn8UXgbZAWGpoFvryV1Pf83HBofuQ7 	pubkey
	
	// sender
	// cMfgA4EMpLbsqeP9rHaxKcUZPosWaBu9mazGpzNKt55wMoBh7tKn 	pubkey
	// cUhfnXh7TYF95HgZXGzcoU32b6ftiSV1PERE5fcwsLdqLeHrK4qS 	privkey
	// ["createTrans","PID","10000","cMg7JyLRn3MGxiWcv9bv66dn8UXgbZAWGpoFvryV1Pf83HBofuQ7","R","100000000000","10000","10000","10000","","AN1rKvt1q7"
	//    ,"AN1rKvtmeJbMuzgFYdyPLNHd1EgL6Ss6snNrDqyxJdTh2Lukg6zAv93gZa5Hao6RNALozoPVvDsGNWLS2uX3J1htviwwZNjT3","cMfgA4EMpLbsqeP9rHaxKcUZPosWaBu9mazGpzNKt55wMoBh7tKn"]
	
	DecimalFormat df = new DecimalFormat("##.##") ;
	if(tryCnt>0) {
		double timeSum = 0;
		for(int i=0; i<tryCnt; i++) {
				double startTime = 0;
				double endTime = 0;
				
				startTime = System.currentTimeMillis(); 
				
				String[] args2 = null;
				args2 = new String[] {"PID","10000","cMhRZxunbKX4Hb4zgWnvJw7cbnQ1GAsPCJhN5MFtEv3cNFhUzqVj","1","","",""};
				ApiHelper.putQuerySignature(args2,false,"query",ApiHelper.EC_CHAIN,KeyManager.getPrivateKey(KeyManager.CPAPI_LEVEL.emMan300));
				
				JSONObject joRes_temp = CPWalletUtil.getValue(ApiHelper.EC_CHAIN,"queryNTransHistory", args2);
				JSONObject joValue = null;
				String strValue = joRes_temp.get("value").toString();
				String npid = joRes_temp.get("npid").toString();

				JSONParser jaTemp = new JSONParser();
				try {
					joValue = (JSONObject)jaTemp.parse(strValue);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				JSONArray jjj = (JSONArray)joValue.get("Record");
				JSONObject sig = (JSONObject)jjj.get(0);
				
				String[] args = null;
				args = new String[] {"PID","10000","cMhB5nGyYPG4GzRnLRATSvgD5rd3PSis6kKxUbFtbw3naw5EzXHt","R","10","Green tea","Nora","John","","",""};
				InMemoryPrivateKey priKey = InMemoryPrivateKey.fromBase58String("cMhjAAfYD1JfBsTG6dkd37czEBnwDLT3kPDsqwrnTC9HK3tiXQ3b", NetConfig.defaultNet);
				if(!ApiHelper.putSignatureWithNonce(args, (String)(sig.get("sig")), priKey)) {
					//return null;
				}
				
				JSONObject joQuery = new JSONObject();

				joQuery.put("chaincode",ApiHelper.EC_CHAIN);
				joQuery.put("query_type","invoke");
				joQuery.put("func_name","createTrans");
				joQuery.put("npid","");

				JSONArray jaParam = new JSONArray();
				for( String strArg : args) {
					jaParam.add(strArg);
				}
				System.out.println("jaParam : "+jaParam);
				joQuery.put("func_args", jaParam);  
				
				JSONObject joRes = ApiHelper.postJSON(joQuery);
				
				System.out.println("joRes : "+joRes.toString());
				if(joRes != null) {
					long nCode = (Long)joRes.getOrDefault("ec",-1L);
					System.out.println(" nCode : "+nCode);
				}
				endTime = System.currentTimeMillis();
				double term = (endTime/1000) - (startTime/1000);
				%>
				<script>$('#cons').prepend("<%=df.format(term)+" 초 "+ ", npid : "+npid+"<br/>" %>");</script>
				<%				
				timeSum+=term;
		} %>
		
		전체 경과 시간 : <%=df.format(timeSum)+" 초" %><br/>
		평균 시간 : <%=df.format((timeSum/tryCnt))+" 초" %><br/>
	<% } %>
</body>
</html>