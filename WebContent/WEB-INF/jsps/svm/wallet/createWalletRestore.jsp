<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO" %>
<%@ page import="io.nodehome.svm.common.util.EtcUtils"%>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.util.DateUtil"%>
<%@ page import="io.nodehome.cmm.FouriMessageSource"%>
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
    <script src="/js/tapp_interface.js"></script>    
	<script>
	$.ajaxSetup({ async:false }); // AJAX calls in order
	
	// Script to run as soon as loaded from the web
	$(function() {
	});
	
	// Function to call as soon as it is loaded from the App
	function AWI_OnLoadFromApp(dtype) {
		// Activate AWI_XXX method
		AWI_ENABLE = true;
		AWI_DEVICE = dtype;
		AWI_setAccountConfig("<%=strAWID%>","wallet");
	}

	function FnCreateWallet() {
		if(createWalletRestoreForm.walletName.value == "") {
			alertMassage('<spring:message code="user.button.input.wallet.name" />');
			return;
		}
		sWID=createWalletRestoreForm.wid.value;
		sName=createWalletRestoreForm.walletName.value;
		AWI_setAccountConfig(sWID,sName);
		location.href = "/index";
	}
	</script>
  	</head>
  
  
<body>

	<div class="container">

		<div class="row">
		    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
				<form name="createWalletRestoreForm" id="createWalletRestoreForm" method="post" onsubmit="return false;">
				<input type="hidden" name="wid" id="wid" value="<%=strAWID%>" />        
				<h2>Wallet Restore Name <br /><small>Please enter your wallet name</small></h2>
				<hr class="colorgraph">
				
				<dl>
					<dt><h2><small><spring:message code="body.text.restore.wallet.address" /></small></h2></dt>
					<dd class="oaerror warning" style="word-break:break-all;"><%=strAWID%></dd>
				</dl>
				
				<dl>
					<dt><h2><small>임시 지갑명</small></h2></dt>
					<dd class="oaerror warning" style="word-break:break-all;">
					<p>지갑명 : wallet</p>
					<p style="color:blue;">희망하는 지갑명을 설정하세요.</p>
					</dd>
				</dl>
				
				<div class="form-group">
					<input type="text" name="walletName" id="walletName" class="form-control input-lg" placeholder="Input Wallet Name" maxlength="30" tabindex="1" />
				</div>
				
				<hr class="colorgraph">
				<div class="row">
					<div class="col-xs-12"><input type="button" value="<spring:message code="user.text.create" />" class="btn btn-success btn-lg btn-block" onclick="FnCreateWallet();" tabindex="2" /></div>
				</div>
				<div style="padding-bottom:20px;"></div>
				</form>
			</div>
		</div>
	</div>
	
</body>

</html>