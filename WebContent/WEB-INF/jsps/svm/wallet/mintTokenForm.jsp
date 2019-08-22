<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>

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

	var myBalance = 0;
	
	var coinData;
	var policyData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
	// Script to run as soon as loaded from the web
	$(function() {
	});
	
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
		coinData = WSI_callJsonAPI("/svm/common/getCoinInfo", sQuery);
		// get policy info
		policyData = WSI_callJsonAPI("/svm/common/getPolicyInfo", sQuery);
		
		j_curWID = AWI_getAccountConfig("CUR_WID");
		
		myBalance = getBalance(j_curWID);
	}

	function getBalance(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		var rtnBalance = 0;	// Balance
		
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
				rtnBalance = retData['balance'];
			} else {
				return '';
			}
		}
		return rtnBalance;
	}

	function FnMintToken() {

 		var tokenId = frm.tokenId.value;
 		var amount = frm.amount.value;
 		var memo = frm.memo.value;

		if(myBalance < parseInt(policyData['FeeMin'])) {
			$.alert('잔액이 부족합니다.');
			return false;
		}
 		if(amount=="" || amount=="0") {
 			$.alert('발행량을 입력하세요.');
 		}
 		
		$('#loading').show();
		setTimeout(function() {

			var sArgs = ["PID","10000",tokenId, amount,j_curWID,  memo];
			var displayParam = [];
			AWI_runTransaction("mintToken", sArgs, "FnCallBackFunction", "Create Token", "", displayParam, j_curWID, parseInt(policyData['FeeMin']));
			
		},50);
	}

	function FnCallBackFunction(strJson) {
		var joRoot = JSON.parse(strJson);
		var joFunc = joRoot['func'];
		if(joFunc['result'] == "OK") {
			$.alert({
			    title: '안내',
			    content: "Token발행이 완료 되었습니다.",
			    confirm: function(){
			    },
			    onClose: function(){
					location.href = "/index?wid="+j_curWID;
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
    
	<div id="loading" class="loading" style="display:none;"><div class="loading-icon">
	  <div class="loading-bar"></div>
	  <div class="loading-bar"></div>
	  <div class="loading-bar"></div>
	  <div class="loading-bar"></div>
	</div></div>

	<div class="container">

		<Br/>
		<div class="row">
		    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
				<form name="frm" id="frm" method="post" onsubmit="return false;">
				<input type="hidden" name="nonce" id="nonce" />
		        <input type="hidden" name="npid" id="npid" />		        
				<h2>Token Issue <br /><small></small></h2>
				<hr class="colorgraph">
					
	            <div class="form">
	                <div class="form-row">
	                  <div class="form-group col-lg-6">
	                    <input type="text" class="form-control" name="tokenId" id="tokenId" value="<%=token_id %>" <%if(!token_id.equals("")){ %>readonly<%} %> />
	                  </div>
	                  <div class="form-group col-lg-6">
	                    <input type="text" class="form-control" name="amount" id="amount" placeholder="발행량"  />
	                  </div>
	                </div>
	                <div class="form-group">
	                  <textarea class="form-control" name="memo" id="memo" rows="5" placeholder="Memo"></textarea>
	                </div>
					<div style="" class="row">
						<div class="col-6" style="padding-right:3px;"><div style="padding:2px 5px 2px 5px;background-color:#4A76D9;border-radius:15px;color:#fff;text-align:center;" onclick="FnMintToken();">발행</div></div>
						<div class="col-6" style="padding-left:3px;"><div style="padding:2px 5px 2px 5px;background-color:#4A76D9;border-radius:15px;color:#fff;text-align:center;" onclick="FnMainGo();"><spring:message code="body.button.RimitCancel" /></div></div>
					</div>
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