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
String strAWID = request.getParameter("wid");
if (strAWID == null) strAWID = "";
%>
<!DOCTYPE html>
<html>
	<head>
    <title></title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    
    <!-- bootstrap 3.3.7 -->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link href="/bootstrap/assets/css/material-common.css" rel="stylesheet">
    
	<link rel="stylesheet" href="/css/jquery-confirm.min.css">
	<script src="/js/jquery-confirm.min.js"></script>
	
    <link href="/css/loading.css" rel="stylesheet" />
    
    <!-- Platform JS -->
    <script src="/js/loader.js"></script>
    <script src="/js/tapp_interface.js"></script>
    <script src="/js/common.js"></script>
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
		var coinData = WSI_callJsonAPI("/svm/common/getCoinInfo", sQuery);

		minRemittance = coinData['mnt'];
		
		j_curANM = AWI_getAccountConfig("ACCOUNT_NM");
		j_curWID = AWI_getAccountConfig("CUR_WID");
		j_curWNM = AWI_getAccountConfig(j_curWID);	
	
		// document.getElementById("s_account_name").innerHTML = "<spring:message code="body.msg.RimitName" /> : " + j_curANM;
		document.getElementById("s_account_name").value = j_curANM;

		var elmtTBody = $('#id_walletlist_selectbox_option'); //Withdrawal wallet
		var sHTML="";
		// sHTML += '<input type="hidden" id="wallet_id" name="wallet_id" value="'+j_curWID+'"/>';
		// sHTML += j_curWNM;
		// elmtTBody.html(sHTML);
		document.getElementById("wallet_id").value = j_curWID;
		document.getElementById("id_walletlist_selectbox_option").value = j_curWNM;
		 
		// getNonce
		myBalance = getBalance(j_curWID);
		<%-- $("#myPageUserAmt").value(gfnAddComma(myBalance)+" <%=CoinListVO.getCoinCou()%>"); --%>
		document.getElementById("myPageUserAmt").value = ""+gfnAddComma(myBalance/<%=CoinUtil.DISPLAY_COIN_UNIT%>)+" "+ coinData['cou'];
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
				return 0;
			}
		}
		return rtnBalance;
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

		var sQuery = {"walletID" : pWalletId,"netType":j_curNetId,"chainID":j_chainID};
		retData = WSI_callJsonAPI("/svm/wallet/queryWalletInfo", sQuery);

		if(retData['result'] == "OK") {
			rtnNm = retData['nm'];
		} else {
			return '';
		}
		
		return rtnNm;
	}

	// Terminal Confirm page process
	function FnTransferCoin() {
		$('#loading').css('display','block');

		setTimeout(function () {
		    var transferContent = "coin transaction"; // Transaction content
		    var remittanceAmount = ($("#id_RemittanceAmount").val()) * ("<%=CoinUtil.DISPLAY_COIN_UNIT%>"); // Remittance amount
		    var withdrawMemo = document.getElementById ("id_withdrawMemo"). value; // Withdrawal note
		    var depositMemo = document.getElementById ("id_depositMemo"). value; // deposit note
		    
		    if (eval(minRemittance) > (parseInt(remittanceAmount)*1000)) {
		    	$.alert('최소 송금액은 '+minRemittance+' <%=CoinUtil.DISPLAY_COIN_UNIT%> 입니다.'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
			var pWalletId = j_curWID;
		    if(j_qrCodeValue == "" && userWalletRimitFom.walletName2.value=="") {
		    	$.alert('<spring:message code="body.msg.nowallet" />'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
		    if(j_qrCodeValue===undefined || j_qrCodeValue == "") {
		    	j_qrCodeValue = userWalletRimitFom.walletName2.value;
		    }

		    if (eval(remittanceAmount) > eval(myBalance)) {
		    	$.alert('잔액이 부족합니다.'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
		    // node 운영자 지갑 id 파라메터에 추가 예정 : <%=ServiceWalletVO.getWalletId() %>
		    
			var sArgs = ["PID","10000",pWalletId,j_qrCodeValue,"R",remittanceAmount,transferContent,withdrawMemo,depositMemo];
			var displayParam = [];
			var trRes = AWI_runTransaction("sendCoin", sArgs, "FnCallBackTransferCoin", "송금확인", "", displayParam, j_curWID, 0);
		}, 10);
	}
	
	function FnCallBackTransferCoin(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot.func;
		if(joFunc['result'] == "OK") {
			alert("송금처리가 완료 되었습니다.");
			location.href="/index";
		} else if(joFunc['result'] == "CANCEL") {
			;
		} else {
			var resValue = joFunc['value'];
			alert("송금이 실패 했습니다. [" + resValue['ref']+"]");
		}
		$('#loading').css('display','none');
	}

	// WEB Confirm page process
	function FnWebTransferCoin() {
		$('#loading').css('display','block');

		setTimeout(function () {
		    var transferContent = "coin transaction"; // Transaction content
		    var remittanceAmount = ($("#id_RemittanceAmount").val()) * ("<%=CoinUtil.DISPLAY_COIN_UNIT%>"); // Remittance amount
		    var withdrawMemo = document.getElementById ("id_withdrawMemo"). value; // Withdrawal note
		    var depositMemo = document.getElementById ("id_depositMemo"). value; // deposit note
		    
		    if (eval(minRemittance) > eval($("#id_RemittanceAmount").val())) {
		    	$.alert('error 1'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
			var pWalletId = j_curWID;
		    if(j_qrCodeValue == "" && userWalletRimitFom.walletName2.value=="") {
		    	$.alert('<spring:message code="body.msg.nowallet" />'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
		    if(j_qrCodeValue===undefined || j_qrCodeValue == "") {
		    	j_qrCodeValue = userWalletRimitFom.walletName2.value;
		    }

		    if (eval(remittanceAmount) > eval(myBalance)) {
		    	$.alert('잔액이 부족합니다.'); // error
		    	$('#loading').css('display','none');
		    	return false;
		    }
		    
			var sArgs = ["PID","10000",pWalletId,j_qrCodeValue,"R",remittanceAmount,transferContent,withdrawMemo,depositMemo];
			AWI_transferWebTransactionConfirm("sendCoin", sArgs, j_curWID, "FnCallBackWebTransfer1");
		}, 10);
	}
	
	function FnCallBackWebTransfer1(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot.func;
		if(joFunc['result'] == "OK") {
			var val = joFunc['value']['value']['val'];
			var fee = joFunc['value']['value']['fee'];
			if(val != null && fee != null && val != "" && fee != "") {
				$.confirm({
				    title: '확인',
				    content: "수수료 "+(parseInt(fee)/parseInt("<%=CoinUtil.DISPLAY_COIN_UNIT%>"))+"를 포함하여 "+((parseInt(fee)+parseInt(val))/parseInt("<%=CoinUtil.DISPLAY_COIN_UNIT%>"))+ coinData['cou'] + " 이 송금됩니다.",
				    buttons: {
				    	confirm: {
		    	            text: "확인",
		    	            btnClass: 'btn-danger',
		    	            action: function(){
		    	            	FnWebTransferCoin2();
		    	            }
				    	},
				    	cancel: {
		    	            text: '취소',
		    	            btnClass: 'btn-default',
		    	            action: function(){
		    	        		$('#loading').css('display','none');
		    	            }
				    	}
				    },
					onAction: function (btnName) {
						;
					},

				});
				
			} else {
				$.alert('Fail send transfer');
				$('#loading').css('display','none');
			}
		} else if(joFunc['result'] == "CANCEL") {
			$('#loading').css('display','none');
		} else {
			$('#loading').css('display','none');
		}
	}

	// WEB Confirm page process
	function FnWebTransferCoin2() {
	    var transferContent = "coin transaction"; // Transaction content
	    var remittanceAmount = ($("#id_RemittanceAmount").val()) * ("<%=CoinUtil.DISPLAY_COIN_UNIT%>"); // Remittance amount
	    var withdrawMemo = document.getElementById ("id_withdrawMemo"). value; // Withdrawal note
	    var depositMemo = document.getElementById ("id_depositMemo"). value; // deposit note
	    
		var pWalletId = j_curWID;
	    
		var sArgs = ["PID","10000",pWalletId,j_qrCodeValue,"R",remittanceAmount,transferContent,withdrawMemo,depositMemo];
		AWI_transferWebTransaction("sendCoin", sArgs, j_curWID, "FnCallBackWebTransfer2");
	}
	function FnCallBackWebTransfer2(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot.func;
		if(joFunc['result'] == "OK") {
			location.href="/index";
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
				      <div class="input-group-addon"><spring:message code="body.text.my.account" /></div>
				      <input type="text" class="form-control input-lg" id="s_account_name" readonly />
				    </div>
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon"><spring:message code="body.text.walletName" /></div>
				      <input type="text" class="form-control input-lg" id="id_walletlist_selectbox_option" readonly />
				    </div>
				</div>
                
                <div class="form-group">
					<div class="input-group">
				      <div class="input-group-addon"><spring:message code="user.text.Balance" /></div>
				      <input type="text" class="form-control input-lg" id="myPageUserAmt" readonly />
				    </div>
				</div>
			        
			    
			    <div class="form-group">
					<input type="text" name="id_withdrawMemo" id="id_withdrawMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemo" />" tabindex="1" />
					<%-- <div class="input-group">
				      <div class="input-group-addon"><spring:message code="body.text.walletMemo" /></div>
				      <input type="text" name="id_withdrawMemo" id="id_withdrawMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemo" />" tabindex="1" />
				    </div> --%>
				</div>
				
				<div class="form-group">
					<div class="input-group">
				      <%-- <div class="input-group-addon"><spring:message code="body.text.walletNameDeposit" /></div> --%>
				      <input type="text" name="walletName2" id="walletName2" class="form-control input-lg" placeholder="<spring:message code="body.text.walletNameDeposit" />" tabindex="2" />
				      <div class="input-group-addon"><input type="button" class="btn btn-warning btn-sm pull-center" value="<spring:message code="body.text.qrcode" />" onclick="FnScanQRCode();"></div>
				    </div>
				</div>
				
				<div class="form-group">
					<input type="text" name="id_depositMemo" id="id_depositMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemoDeposit" />" tabindex="3" />
					<%-- <div class="input-group">
				      <div class="input-group-addon"><spring:message code="body.text.walletMemoDeposit" /></div>
				      <input type="text" name="id_depositMemo" id="id_depositMemo" class="form-control input-lg" placeholder="<spring:message code="body.text.walletMemoDeposit" />" tabindex="3" />
				    </div> --%>
				</div>
				
				<div class="form-group">
					<%-- <script>
					document.write('<spring:message code="body.text.min.send.amount" /> : <%=minRemittance %> '+coinData['cou']+'<br/>');
					var maxc = coinData['mxt'];
					if(maxc!=0) {
						document.write('<spring:message code="body.text.max.send.amount" /> : ' + (maxc/parseInt("<%=CoinUtil.DISPLAY_COIN_UNIT %>")) + ' ' + coinData['cou'] + '<br/>');
					}
					</script> --%>
					<input type="text" name="id_RemittanceAmount" id="id_RemittanceAmount" class="form-control input-lg" placeholder="<spring:message code="body.text.walletAmountDeposit" />" tabindex="4" />
					<%-- <div class="input-group">
				      <div class="input-group-addon"><spring:message code="body.text.walletAmountDeposit" /></div>
				      <input type="text" name="id_RemittanceAmount" id="id_RemittanceAmount" class="form-control input-lg" placeholder="<spring:message code="body.text.walletAmountDeposit" />" tabindex="4" />
				    </div> --%>
				</div>
				
				<hr class="colorgraph">
				<div class="row">
					<div class="col-xs-6"><input type="button" value="<spring:message code="body.button.Rimit" />" class="btn btn-success btn-lg btn-block" onclick="FnTransferCoin();" tabindex="5" /></div>
					<div class="col-xs-6"><input type="button" value="<spring:message code="body.button.RimitCancel" />" class="btn btn-primary btn-lg btn-block" onclick="FnMainGo();" tabindex="6" /></div>
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
				
	</div>
	
</body>

</html>