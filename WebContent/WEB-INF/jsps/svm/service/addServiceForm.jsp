<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO" %>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
<%@ page import="io.nodehome.svm.common.biz.ApiHelper"%>

<%@ include file="/inc/alertMessage.jsp" %>
<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

String serviceFeeB = CoinListVO.getServiceRegFee();
String serviceFee = CoinUtil.calcDisplayCoin(Double.parseDouble(serviceFeeB));
String projectServiceid = GlobalProperties.getProperty("project_serviceID");

String localServiceHost = request.getRequestURL().toString();
localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
%>
<!DOCTYPE html>
<html>
	<head>
    <title></title>
    <!-- Required meta tags -->
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />    
    
    <!-- bootstrap 3.3.7 -->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link href="/bootstrap/assets/css/material-common.css" rel="stylesheet">
    
    <link href="/css/loading.css" rel="stylesheet" />
    
    <!-- Platform JS -->
    <script src="/js/loader.js"></script>
    <script src="/js/tapp_interface.js"></script>
    <script src="/js/common.js"></script>
    
	<link rel="stylesheet" href="/css/jquery-confirm.min.css">
	<script src="/js/jquery-confirm.min.js"></script>
	
	<script>
	$.ajaxSetup({ async:false }); // AJAX calls in order
	
	// Script to run as soon as loaded from the web
	$(function() {
	});

	var j_curANM = "";
	var j_curWID = "";
	var j_curWNM = "";
	var j_curNetId;
	var j_chainID;
	var j_seedHost;

	// Function to call as soon as it is loaded from the App
	var myBalance = 0;
	
	function AWI_OnLoadFromApp(dtype) {
		// Activate AWI_XXX method
		AWI_ENABLE = true;
		AWI_DEVICE = dtype;

		// Terminal -> sapp chain정보
		var seedInfo = AWI_getSeedHostInfo();
	    if(seedInfo['result']=='OK') {
			 j_curNetId = seedInfo['netID'];
			 j_chainID = seedInfo['chainID'];
			 j_seedHost = seedInfo['seedHost'];
	    } else {
	    	 $.alert('체인 정보를 로딩하는데 실패 했습니다.');
	    }

		// get Coin info
		var sQuery = {"netType":j_curNetId,"chainID":j_chainID};
		var coinData = WSI_callJsonAPI("/svm/common/getCoinInfo", sQuery);

		j_curANM = AWI_getAccountConfig("ACCOUNT_NM");
		j_curWID = AWI_getAccountConfig("CUR_WID");
		j_curWNM = AWI_getAccountConfig(j_curWID);	
		 
		myBalance = getBalance(j_curWID);
		document.getElementById("s_account_name").value = j_curANM;
	}

	function getBalance(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		var rtnBalance = 0;	// Balance
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"PID", "ver":"10000", "serviceID":"<%=projectServiceid%>"};
		var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
		if(retData['result'] == "OK") {
			sNonce = retData['nonce'];
			sNpid = retData['npid'];
		} else {
			return false;
		}
		
		// ************ step2 : get Signature / S-T API
		sQuery = ["PID","10000",sNonce];
		var sigRes = AWI_getSignature(pWalletId, sQuery,"query","getBalance");
		if(sigRes['result']=="OK") {
			sSig = sigRes['signature_key'];	
			
			// ************ step3 : get Balance / SVM API
			sQuery = {"npid":sNpid, "serviceID":"<%=projectServiceid%>", "parameterArgs" : ["PID","10000",sNonce,sSig,pWalletId]};
			retData = WSI_callJsonAPI("/svm/wallet/getBalance", sQuery);
			if(retData['result'] == "OK") {
				rtnBalance = retData['balance'];
			} else {
				return '';
			}
		}
		return rtnBalance;
	}
	
	function FnAddService() {
		var sNonce = "";
		var sNpid = "";
		
		var pWalletId = j_curWID;

	    var transferContent = "add service";
	    var serviceRegFee =  parseInt("<%=serviceFeeB%>");
	    var sServiceName = document.getElementById ("s_service_name").value;
	    var sServiceMemo = document.getElementById ("s_service_memo").value;
	    sServiceMemo = sServiceMemo.replace(/(?:\r\n|\r|\n)/g, '<br/>');

	    if(sServiceName=="") {alert('<spring:message code="service.require.alert1" />');return;}
	    if(sServiceMemo=="") {alert('<spring:message code="service.require.alert4" />');return;}
	    if (eval(serviceRegFee) > eval(myBalance)) {
	    	$.alert('<spring:message code="service.msg.nobalance" /> '+serviceRegFee+' '+coinData['cou']); // error
	    	return false;
	    }
	    
	    var result = confirm("<spring:message code="service.msg.remitSubmit" /> " +(parseInt(serviceRegFee)/1000)+ " "+coinData['cou']);
	    if(result) {
			$('#loading').css('display','block');

			setTimeout(function () {
				if (result) {
					var sQuery = {"pid":"PID", "ver":"10000", "cType":"<%=ApiHelper.EC_CHAIN%>", "serviceID":"<%=projectServiceid%>"};
					var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
					if(retData['result'] == "OK") {
						sNonce = retData['nonce'];
						sNpid = retData['npid'];
					} else {
						$('#loading').css('display','none');
						return false;
					}
					
					sQuery = ["PID","10000","{\"sOwner\":\""+pWalletId+"\",\"ServiceName\":\""+sServiceName+"\",\"ServiceMemo\":\""+sServiceMemo+"\"}",sNonce];
					var sigRes = AWI_getSignature(pWalletId, sQuery,"invoke","registerService");
					
					if(sigRes['result']=="OK") {
						sSig = sigRes['signature_key'];
					}

					var sArgs = ["PID","10000","{\\\"sOwner\\\":\\\""+pWalletId+"\\\",\\\"ServiceName\\\":\\\""+sServiceName+"\\\",\\\"ServiceMemo\\\":\\\""+sServiceMemo+"\\\"}",sNonce,sSig,pWalletId];
					sQuery = {"requestUrl":j_seedHost+"/createService", "npid":sNpid, "addServiceName":sServiceName, "serviceID":"<%=projectServiceid%>", "parameterArgs" : sArgs};
					retData = WSI_callCorsJsonAPI(sQuery);
					if (retData['result'] == "OK") {
						$.alert({
							    title: '안내',
							    content: "<spring:message code="service.msg.add.complete" /> "+retData['serviceID'],
							    confirm: function(){
							    },
							    onClose: function(){
									history.back();
							    },
						});
		            } else {
						$('#loading').css('display','none');
						$.alert("처리중 오류가 발생했습니다.("+joRes['strValue']+")");
		            	return false;
		            }
					
				} else {
					$.alert("처리중 오류가 발생했습니다.");
					$('#loading').css('display','none');
					return false;
				}
			},10);
	    }
		
	}
	
	function FnMainGo() {
		location.href = "/index?wid="+j_curWID;
	}
	</script>
  	</head>
  
  
<body>

	<div class="container">
		
		<div id="loading" class="loading" style="display:none;"><img id="loading_img" alt="loading" src="/images/viewLoading.gif" /></div>
		
		<div class="row">
		    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
				
				<form name="frm" id="frm" method="post" onsubmit="return false;">
				<input type="hidden" name="nonce" id="nonce" />
		        <input type="hidden" name="npid" id="npid" />
		        <input type="hidden" id="wallet_id" name="wallet_id" />		        
				<h2><spring:message code="service.page.title" /> <br /><small></small></h2>
				<hr class="colorgraph">
				
			    <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon"><spring:message code="service.text.ownername" /></div>
				      <input type="text" class="form-control input-lg" id="s_account_name" readonly/>
				    </div>
				</div>
				
			    <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon"><spring:message code="service.text.servicename" /></div>
				      <input type="text" class="form-control input-lg" id="s_service_name" />
				    </div>
				</div>
				
			    <div class="form-group">
					<textarea name="s_service_memo" id="s_service_memo" class="form-control input-lg" placeholder="<spring:message code="service.text.servicedesc" />" rows="5"></textarea>
				</div>
				
				<hr class="colorgraph">
				<div class="row">
					<div class="col-xs-6"><input type="button" value="<spring:message code="service.text.btn.add" />" class="btn btn-success btn-lg btn-block" onclick="FnAddService();" /></div>
					<div class="col-xs-6"><input type="button" value="<spring:message code="service.text.btn.cancel" />" class="btn btn-primary btn-lg btn-block" onclick="FnMainGo();" /></div>
					<!-- <spring:message code="body.button.RimitCancel" /> -->
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
				
	</div>
	
</body>

</html>