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
<%@ page import="io.nodehome.svm.common.biz.ServiceWalletVO"%>

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
String token_id = request.getParameter("token_id");
if (token_id == null) token_id = "";
%>
<!DOCTYPE html>
<html>

  <head>
    <title></title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,700,700i|Montserrat:300,400,500,700" rel="stylesheet">

  <!-- Bootstrap CSS File -->
  <link href="/lib/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Libraries CSS Files -->
  <link href="/lib/font-awesome/css/font-awesome.min.css" rel="stylesheet">
  <link href="/lib/animate/animate.min.css" rel="stylesheet">
  <link href="/lib/ionicons/css/ionicons.min.css" rel="stylesheet">
  <link href="/lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">
  <link href="/lib/lightbox/css/lightbox.min.css" rel="stylesheet">

  <script src="/lib/jquery/jquery.min.js"></script>
  
  <!-- Main Stylesheet File -->
  <link href="/css/style.css" rel="stylesheet">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" crossorigin="anonymous">

    <link rel="stylesheet" href="/css/jquery-confirm.min.css">
	<script src="/js/jquery-confirm.min.js"></script>
	
    <script src="/js/common.js"></script>
    <script src="/js/tapp_interface.js"></script>
	<script>
	$.ajaxSetup({ async:false }); // AJAX calls in order
	
	// Script to run as soon as loaded from the web
	$(function() {
	});

	var j_curANM = "";
	var j_curWID = "";
	var j_curWNM = "";
	
	var coinData;
	var policyData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;

	// Function to call as soon as it is loaded from the App
	var myBalance = 0;
	var myTokenBalance = 0;
	var myAbleTokenBalance = 0;
	// Minimum remittance amount
	
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

		j_curANM = AWI_getAccountConfig("ACCOUNT_NM");
		j_curWID = AWI_getAccountConfig("CUR_WID");
		j_curWNM = AWI_getAccountConfig(j_curWID);	
		
		// document.getElementById("s_account_name").innerHTML = "<spring:message code="body.msg.RimitName" /> : " + j_curANM;
		document.getElementById("s_account_name").value = j_curANM;
		document.getElementById("wallet_id").value = j_curWID;
		document.getElementById("id_walletlist_selectbox_option").value = j_curWNM;
		 
		// getNonce
		getBalance(j_curWID);
		getTokenBalance(j_curWID);
		document.getElementById("myPageUserAmt").value = gfnAddComma(myTokenBalance);
		$('#maxSendAmount').html(gfnAddComma(myAbleTokenBalance));
	}

	function getBalance(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"PID", "ver":"10000", "nType":"query", "netType":j_curNetId,"chainID":j_chainID};
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
			sQuery = {"npid":sNpid, "parameterArgs" : ["PID","10000",sNonce,sSig,pWalletId], "netType":j_curNetId,"chainID":j_chainID};
			retData = WSI_callJsonAPI("/svm/wallet/getBalance", sQuery);
			if(retData['result'] == "OK") {
				myBalance = retData['balance'];
			}
		}
	}
	
	function getTokenBalance(pWalletId) {
		sQuery = {"walletID":pWalletId, "tokenId" : "<%=token_id%>", "netType":j_curNetId,"chainID":j_chainID};
		retData = WSI_callJsonAPI("/svm/token/queryTokenBalance", sQuery);
		
		if(retData['result'] == "OK") {
			rtnValue = retData['value'];
			if(rtnValue!==undefined && rtnValue.length>0) {
				myTokenBalance = (rtnValue[0])['balance'];
				myAbleTokenBalance = (rtnValue[0])['available_balance'];
			}
		}
	}
	
	function FnScanQRCode() {
		var joCmd = null;
		var params = new Object();
		params['cmd'] = "getQRCodeByScan";
		params['callbackFunc'] = "FnScanQRCodeCallBack";
		params['param'] = "";
		joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
	}
	var j_qrCodeValue = "";
	function FnScanQRCodeCallBack(strJson) {
		var joRoot = JSON.parse(strJson);  
		var joFunc = joRoot.func;
		
		j_qrCodeValue = joFunc.value;
		joResNm = loadWalletName(j_qrCodeValue);
		if(joResNm!='')
			userWalletRimitFom.walletName2.value = joResNm;
		else 
			userWalletRimitFom.walletName2.value = j_qrCodeValue;
	}
	
	// qr reading default call back function
	function AWI_CallFromApp(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot.func;
		if(joFunc.cmd == 'qrCodeReader') {
			j_qrCodeValue = joFunc.value;
			joResNm = loadWalletName(j_qrCodeValue);
			if(joResNm!='')
				userWalletRimitFom.walletName2.value = joResNm;
			else 
				userWalletRimitFom.walletName2.value = j_qrCodeValue;
		}
	}

	function loadWalletName(pWalletId) {
		var rtnNm;

		var sQuery = {"walletID" : pWalletId, "netType":j_curNetId,"chainID":j_chainID};
		retData = WSI_callJsonAPI("/svm/wallet/queryWalletInfo", sQuery);

		if(retData['result'] == "OK") {
			rtnNm = retData['nm'];
		} else {
			return '';
		}
		
		return rtnNm;
	}
	
	// submit send coin 
	// When you send money from Terminal
	function FnTransferCoin() {
		$('#loading').css('display','block');

		setTimeout(function () {
		    var transferContent = "coin transaction"; // Transaction content
		    var remittanceAmount = ($("#id_RemittanceAmount").val()); // Remittance amount
		    var withdrawMemo = document.getElementById ("id_withdrawMemo"). value; // Withdrawal note
		    var depositMemo = document.getElementById ("id_depositMemo"). value; // deposit note
		    
			var pWalletId = j_curWID;
		    if(j_qrCodeValue == "" && userWalletRimitFom.walletName2.value=="") {
		    	$.alert('<spring:message code="body.msg.nowallet" />'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
		    if(j_qrCodeValue===undefined || j_qrCodeValue == "") {
		    	j_qrCodeValue = userWalletRimitFom.walletName2.value;
		    }
			if(myBalance < parseInt(policyData['FeeMin'])) {
				$.alert('코인 잔액이 부족합니다.');
		    	$('#loading').css('display','none');
				return false;
			}
			if(myAbleTokenBalance < remittanceAmount) {
				$.alert('토큰 잔액이 부족합니다.');
		    	$('#loading').css('display','none');
				return false;
			}
		    
			var sArgs = ["PID","10000","<%=token_id%>",remittanceAmount,j_qrCodeValue,depositMemo,withdrawMemo];
			var displayParam = [];
			AWI_runTransaction("sendToken", sArgs, "FnCallBackTransferCoin", "송금확인", "", displayParam, j_curWID, parseInt(policyData['FeeMin']));
		}, 10);
	}
	
	function FnCallBackTransferCoin(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot.func;
		if(joFunc['result'] == "OK") {
			$.alert({
			    title: '안내',
			    content: "전송 완료 되었습니다.",
			    confirm: function(){
			    },
			    onClose: function(){
					location.href="/index";
			    },
			});
		} else if(joFunc['result'] == "CANCEL") {
			;
		} else {
			var resValue = joFunc['value'];
		}
		$('#loading').css('display','none');
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
				
				<form name="userWalletRimitFom" id="userWalletRimitFom" method="post" onsubmit="return false;">
				<input type="hidden" name="nonce" id="nonce" />
		        <input type="hidden" name="npid" id="npid" />
		        <input type="hidden" id="wallet_id" name="wallet_id" />		        
				<h2><spring:message code="body.button.Rimit" /> <br /><small>Please enter your remittance</small></h2>
				<hr class="colorgraph">
				
				<%-- <div class="form-group form-inline">
  			    	<span class="input-group-addon" style="font-weight:bold; height:50px;" id="s_account_name"><spring:message code="body.msg.RimitName" /> : </span>
			    </div> --%>
			    
			    <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="line-height:35px;padding-right:5px;"><spring:message code="body.text.my.account" /></div>
				      <input type="text" class="form-control input-lg" id="s_account_name" readonly />
				    </div>
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="line-height:35px;padding-right:5px;"><spring:message code="body.text.walletName" /></div>
				      <input type="text" class="form-control input-lg" id="id_walletlist_selectbox_option" readonly />
				    </div>
				</div>
                
                <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon" style="line-height:35px;padding-right:5px;"><spring:message code="user.text.Balance" /></div>
				      <input type="text" class="form-control input-lg" id="myPageUserAmt" readonly />
				    </div>
				</div>
			    
			    <div class="form-group">
					<input type="text" name="id_withdrawMemo" id="id_withdrawMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemo" />" tabindex="1" />
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <input type="text" name="walletName2" id="walletName2" class="form-control input-lg" placeholder="<spring:message code="body.text.walletNameDeposit" />" tabindex="2" />
				      <div class="input-group-addon" style="background-color:#4A76D9;color:#fff;text-align:center;line-height:35px;font-size:12pt;" onclick="FnScanQRCode();"><spring:message code="body.text.qrcode" /></div>
				    </div>
				</div>
				
				<div class="form-group">
					<input type="text" name="id_depositMemo" id="id_depositMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemoDeposit" />" tabindex="3" />
				</div>
				
				<div class="form-group">
					<spring:message code="body.text.max.send.amount" /> : <span id="maxSendAmount"></span><br/>
					<input type="text" name="id_RemittanceAmount" id="id_RemittanceAmount" class="form-control input-lg" placeholder="<spring:message code="body.text.walletAmountDeposit" />" tabindex="4" />
				</div>
				
				<hr class="colorgraph">
				<div style="" class="row">
					<div class="col-6" style="padding-right:3px;"><div style="padding:2px 5px 2px 5px;background-color:#4A76D9;border-radius:15px;color:#fff;text-align:center;" onclick="FnTransferCoin();"><spring:message code="body.button.Rimit" /></div></div>
					<div class="col-6" style="padding-left:3px;"><div style="padding:2px 5px 2px 5px;background-color:#4A76D9;border-radius:15px;color:#fff;text-align:center;" onclick="FnMainGo();"><spring:message code="body.button.RimitCancel" /></div></div>
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
				
	</div>
	
  <!-- JavaScript Libraries -->
  <script src="/lib/jquery/jquery-migrate.min.js"></script>
  <script src="/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="/lib/mobile-nav/mobile-nav.js"></script>
  <script src="/lib/wow/wow.min.js"></script>
  <script src="/lib/waypoints/waypoints.min.js"></script>
  <script src="/lib/counterup/counterup.min.js"></script>
  <script src="/lib/owlcarousel/owl.carousel.min.js"></script>
  <script src="/lib/isotope/isotope.pkgd.min.js"></script>
  <!-- Contact Form JavaScript File -->
  <script src="/contactform/contactform.js"></script>

  <!-- Template Main Javascript File -->
  <script src="/js/main.js"></script>
</body>

</html>