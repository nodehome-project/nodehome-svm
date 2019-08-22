<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
<%@ page import="java.text.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

// WID
String sWid = request.getParameter("sWid");
if (sWid == null) sWid = "";

double maxRemittance = Double.valueOf(StringUtil.nvl((String)request.getParameter("maxRemittance"),"0"));
String projectServiceid = GlobalProperties.getProperty("project_serviceID");
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
	var j_regWID = "";
	var j_regWNM = "";

	var o_owner = "";
	var o_mvd = "";
	var o_mno = "";
	var o_walletNm = "";

	var coinData;
	var policyData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
	// Function to call as soon as it is loaded from the App
	var myBalance = 0;
	// Minimum remittance amount
	var minRemittance;
	
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
		coinData = WSI_callJsonAPI("/svm/common/getCoinInfo", sQuery);
		// get policy info
		policyData = WSI_callJsonAPI("/svm/common/getPolicyInfo", sQuery);
		
		minRemittance = coinData['mnt'] / parseInt("<%=CoinUtil.DISPLAY_COIN_UNIT%>");
		
		j_curANM = AWI_getAccountConfig("ACCOUNT_NM");
		j_regWID = "<%=sWid%>";
		j_regWNM = AWI_getAccountConfig(j_regWID);
	
		document.getElementById("s_account_name").value = j_curANM;

		document.getElementById("wallet_id").value = j_regWID;
		document.getElementById("wallet_name").value = j_regWNM;
		
		myBalance = getBalance(j_regWID);

		var data = WSI_callJsonAPI("/svm/wallet/queryWalletInfo", {"serviceID":"<%=GlobalProperties.getProperty("project_serviceID")%>", "walletID":j_regWID,"netType":j_curNetId,"chainID":j_chainID} );
		if(data.result=="OK") {
			o_owner = data.owner;
			o_mvd = parseInt(data.mvd)/1000;
			o_mno = data.mno;
			o_walletNm = frm.wallet_name.value;
			
			frm.nickname.value = o_owner;
			frm.daily_tr_limit.value = o_mvd;
			frm.reg_memo.value = o_mno;
		}
	}

	function getBalance(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		var rtnBalance = 0;	// Balance
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"PID", "ver":"10000", "nType":"query","netType":j_curNetId,"chainID":j_chainID};
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
			sQuery = {"npid":sNpid, "parameterArgs" : ["PID","10000",sNonce,sSig,pWalletId],"netType":j_curNetId,"chainID":j_chainID};
			retData = WSI_callJsonAPI("/svm/wallet/getBalance", sQuery);
			if(retData['result'] == "OK") {
				rtnBalance = retData['balance'];
			} else {
				return '';
			}
		}
		return rtnBalance;
	}
	
	// submit send coin 
	// When you send money from SApp
	// Function for web direct payment processing test. I am not going to use the transfer for this.
	function FnAddWallet() {
		var sNonce = "";
		var sNpid = "";
		var feeRegWallet = parseInt(policyData['FeeRegisterWallet']);
		var pWalletId = j_regWID;

		if(myBalance < feeRegWallet) {
			$.alert('잔액이 부족합니다.');
			return false;
		}
		
		
		if(frm.nickname.value == o_owner && frm.daily_tr_limit.value == o_mvd && frm.reg_memo.value == o_mno && frm.wallet_name.value == o_walletNm) {
			$.alert('변경된 정보가 없습니다.');
			return false;
		}

		if(frm.nickname.value == o_owner && frm.daily_tr_limit.value == o_mvd && frm.reg_memo.value == o_mno && frm.wallet_name.value != o_walletNm) {
			AWI_setAccountConfig(j_regWID,frm.wallet_name.value);
			$.alert('지갑명을 변경 했습니다.');
			return false;
		} else {

		    var result = confirm("<spring:message code="wallet.msg.addchain" /> (<spring:message code="title.fee" /> : "+ (policyData['FeeRegisterWallet'] / parseInt('<%=CoinUtil.DISPLAY_COIN_UNIT%>')) +" "+coinData['cou']+")");
			
			setTimeout(function () {
				if (result) {
					$('#loading').css('display','block');
					var walletNm = frm.wallet_name.value;
					var owner = frm.nickname.value;
					var dailyTrLimit = parseInt(frm.daily_tr_limit.value)*1000;
					var regMemo = frm.reg_memo.value;

					AWI_setAccountConfig(j_regWID,walletNm);
					
					var sArgs = ["PID","10000",walletNm,owner,dailyTrLimit,regMemo];
					var displayParam = [];
					sReturn = AWI_runTransaction("registerWallet", sArgs, "FnCallBackRegistWallet", "지갑등록확인", "지갑등록확인", displayParam, j_regWID, feeRegWallet);
				} else {
					$('#loading').css('display','hide');
					return false;
				}
			}, 10);
		}
		
	}
	
	function FnCallBackRegistWallet(strJson) {
		$('#loading').css('display','none');
		var joRoot = JSON.parse(strJson);  
		var joFunc = joRoot.func;
		if(joFunc['result'] == "OK") {
			AWI_setAccountConfig("REG_"+j_regWID,"Y");	// Whether to register in the chain
			location.href="/svm/wallet/myWalletList";
		} else if(joFunc['result'] == "CANCEL") {
			;
		} else {
			var resValue = joFunc['value'];
		}
	}
	
	function FnBack() {
		history.back();
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
				<h2><spring:message code="user.button.addWallet" /> <br /><small><spring:message code="body.text.regist.blockchain.message" /></small></h2>
				<hr class="colorgraph">
				
			    <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="width:100px;"><spring:message code="body.text.my.account" /></div>
				      <input type="text" class="form-control input-lg" id="s_account_name" readonly />
				    </div>
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="width:100px;"><spring:message code="wallet.text.walletname" /></div>
				      <input type="text" class="form-control input-lg" id="wallet_name" name="wallet_name"  />
				    </div>
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="width:100px;"><spring:message code="body.text.nickname" /></div>
				      <input type="text" name="nickname" id="nickname" class="form-control input-lg" placeholder="<spring:message code="body.text.nickname" />" />
				    </div>
				</div>
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="width:100px;"><spring:message code="body.text.daily.transfer.limits" /></div>
				      <input type="text" name="daily_tr_limit" id="daily_tr_limit" class="form-control input-lg" placeholder="<spring:message code="body.text.daily.transfer.limits" />" value="<%if(maxRemittance==0) { out.print("100000000"); } else { out.print(maxRemittance); }%>"/>
				    </div>
				</div>
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="width:100px;"><spring:message code="body.text.reg.memo" /></div>
				      <input type="text" name="reg_memo" id=""reg_memo"" class="form-control input-lg" placeholder="<spring:message code="body.text.reg.memo" />" />
				    </div>
				</div>
				
				<hr class="colorgraph">
				<div class="row">
					<div class="col-xs-6"><input type="button" value="<spring:message code="title.create" />" class="btn btn-success btn-lg btn-block" onclick="FnAddWallet();" tabindex="5" /></div>
					<div class="col-xs-6"><input type="button" value="<spring:message code="body.button.RimitCancel" />" class="btn btn-primary btn-lg btn-block" onclick="FnBack();" tabindex="6" /></div>
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
				
	</div>
	
</body>

</html>