<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.util.EtcUtils"%>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.util.DateUtil"%>
<%@ page import="io.nodehome.cmm.FouriMessageSource"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="java.text.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
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
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />    
    
    <!-- bootstrap 3.3.7 -->
    <link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <link href="/bootstrap/assets/css/material-common.css" rel="stylesheet">
    
    <!-- Platform JS -->
    <script src="/js/loader.js"></script>
    <script src="/js/common.js"></script>
    <script src="/js/tapp_interface.js"></script>  
    
	<link rel="stylesheet" href="/css/jquery-confirm.min.css">
	<script src="/js/jquery-confirm.min.js"></script>
	  
	<script>
	$.ajaxSetup({ async:false }); // AJAX calls in order

	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
	// Script to run as soon as loaded from the web
	$(function() {
	});
	
	var walletList;
	// Function to call as soon as it is loaded from the App
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
		 
		j_curWID = AWI_getAccountConfig("CUR_WID");
		var joReturn = AWI_getWalletList();
		var sResult = JSON.parse(joReturn);
		if(sResult['result']=="OK") {
			walletList = sResult['list'];
		}
		
		setTimeout(function () {
			 var rowSelIdx = 0;
			 if(sResult['result']=="OK" && sResult['list']!="" && sResult['list'].length>0) {
		    	var sHTML="";
		    	var elmtTBody = $('#id_walletlist_table_tbody');

		    	for(var i=0; i < walletList.length ; i ++) {
		    		var vBalance = getBalance(walletList[i]['walletID']);
		    		if(vBalance!="") {
			    		var wallerNm = AWI_getAccountConfig(walletList[i]['walletID']);	
			    		sHTML += '<tr id="id_WalletID_' + i + '" style="background-color:#ffffff;">';
			    		sHTML += '<td>' + wallerNm + '</td>';
			    		sHTML += '<td style="word-break: break-all;"><span style="font-size:16px; font-weight:bold; color:#f58903;">' + gfnAddComma(parseInt( vBalance )/<%=CoinUtil.DISPLAY_COIN_UNIT%>) + '</span> <span style="font-size:14px;" class="aaa"> '+coinData['cou']+'</span></td>';
			    		sHTML += '<td>';
			    		sHTML += '</td>';
			    		sHTML += '</tr>';
		   	  			if (j_curWID == walletList[i]['walletID']) {
		   					rowSelIdx = i;
		   			    }
		    		}
		    	}
		    	elmtTBody.html(sHTML);
		    	//document.getElementById("id_WalletID_"+rowSelIdx).style.backgroundColor = "#d9e8ff";
				$('#loading').css('display','none');
			 } else {
				 $('#loading').css('display','none');
			 }
		}, 10);
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
	
	function FnCreateWallet() {
		if(createWalletForm.walletName.value == "") {
			alertMassage('<spring:message code="user.button.input.wallet.name" />');
			return;
		}
		sName=createWalletForm.walletName.value;

		var bools = true;
		for(var i=0; i < walletList.length ; i ++) {
			if(AWI_getAccountConfig(walletList[i]['walletID']) == sName) {
				bools = false;
			}
		}
		if(!bools) {
			$.alert("같은 지갑명이 존재합니다.");
			return false;
		}
		
		sReturn = AWI_newWallet();
		var joReturn = JSON.parse(sReturn);
		if (joReturn['result'] == 'OK') {
			AWI_setAccountConfig(joReturn['walletID'],sName);
		}
		location.href = "/index?wid="+j_curWID;
	}
	
	function FnMainGo() {
		location.href = "/index?wid="+j_curWID;
	}
	</script>
  	</head>
  
  
<body>

	<div class="container">

		<div class="row">
            <div class="col-lg-12 col-md-12">
              <div class="card">
                <div class="card-header card-header-warning">
					<h2>Create Wallet Form <br /><small>Please enter your wallet name</small></h2>
					<hr class="colorgraph">
                  <h4 class="card-title"><spring:message code="body.text.mywallet.list" /></h4>
                  <p class="card-category">My Wallet List</p>
                </div>
                <div class="card-body table-responsive">
                  <table class="table table-hover">
                    <thead class="text-warning">
                      <th>Name</th>
                      <th>Balance</th>
                      <th></th>
                    </thead>                    
                    <tbody id='id_walletlist_table_tbody'>
					</tbody>					
                  </table>
                </div>
              </div>
            </div>
		    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
				<form name="createWalletForm" id="createWalletForm" method="post" onsubmit="return false;">
				<input type="hidden" name="nonce" id="nonce" />
		        <input type="hidden" name="npid" id="npid" />	
                  <p class="card-category">New Wallet Name</p>	        
				<div class="form-group">
					<input type="text" name="walletName" id="walletName" class="form-control input-lg" placeholder="Input Wallet Name" maxlength="30" tabindex="1" />
				</div>
				
				<hr class="colorgraph">
				<div class="row">
					<div class="col-xs-6"><input type="button" value="<spring:message code="user.text.create" />" class="btn btn-success btn-lg btn-block" onclick="FnCreateWallet();" tabindex="2" /></div>
					<div class="col-xs-6"><input type="button" value="<spring:message code="body.button.RimitCancel" />" class="btn btn-primary btn-lg btn-block" onclick="FnMainGo();" tabindex="3" /></div>
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
	</div>
	
</body>

</html>