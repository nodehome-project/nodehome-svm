<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>

<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

// WID
String strAWID = request.getParameter("wid");
if (strAWID == null) strAWID = "";
System.out.println("wid : "+strAWID);
%>
<!DOCTYPE html>
<html>
  <head>
    <title></title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

    <!--     Fonts and icons     -->
    <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <!-- Material Dashboard CSS -->
    <link rel="stylesheet" href="/bootstrap/assets/css/material-dashboard.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css" integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns" crossorigin="anonymous">
    
    <!-- CSS Just for demo purpose, don't include it in your project -->
    <link href="/bootstrap/assets/demo/demo.css" rel="stylesheet" />
    
    <!-- Platform JS -->    
    <script src="/js/loader.js"></script>
    <script src="/js/common.js"></script>
    <script src="/js/tapp_interface.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    
	<link rel="stylesheet" href="/css/jquery-confirm.min.css">
	<script src="/js/jquery-confirm.min.js"></script>

    <script type="text/javascript">
    	//window.locale = '${pageContext.request.locale}';
  		$.ajaxSetup({ async:false }); // AJAX calls in order
  	
		// Script to run as soon as loaded from the web
		$(function() {
			$('.content').click(function(){
				changeMainMenu(false);
			});
		});

		var j_curANM = "";
		var j_curWID = "";
		var j_curWNM = ""; // Default Wallet ID selected
		var j_terminalVersion;

		var coinData;
		var j_curNetId;
		var j_chainID;
		var j_seedHost;
		
		// Function to call as soon as it is loaded from the App
		function AWI_OnLoadFromApp(dtype) {
			 // Activate AWI_XXX method
			 AWI_ENABLE = true;
			 AWI_DEVICE = dtype;

			 j_curANM = AWI_getAccountConfig("ACCOUNT_NM");
			 j_curWID = AWI_getAccountConfig("CUR_WID");
			 j_curWNM = AWI_getAccountConfig(j_curWID);

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

			 // **************************** 서비스 상태 체크 START
			 var activeService = true;

			 sQuery = {"netType":j_curNetId,"chainID":j_chainID,"serviceID":"<%=GlobalProperties.getProperty("project_serviceID")%>"};
			 data = WSI_callJsonAPI("/svm/common/getServiceInfo", sQuery);
			 serviceInfoValue = data['value'];
			 var serviceState = serviceInfoValue['state'];
			 if(serviceState=='R') {
				alert('서비스 준비중입니다. 실행할 수 없습니다.');
				activeService = false;
				//AWI_closeServiceApp();
			 } else if(serviceState=='U') {
				alert(serviceInfoValue['stopMessage']+' 실행할 수 없습니다.');
				activeService = false;
				//AWI_closeServiceApp();
			 } else if(serviceState=='S') {
				alert(serviceInfoValue['stopMessage']+' 실행할 수 없습니다.');
				activeService = false;
				//AWI_closeServiceApp();
			 } else if(serviceState=='E') {
				alert(serviceInfoValue['stopMessage']+' 실행할 수 없습니다.');
				activeService = false;
				//AWI_closeServiceApp();
			 } else if(serviceState=='A') {
				;
			 } else {
				alert('실행할 수 없습니다.');
				activeService = false;
				//AWI_closeServiceApp();
			 }
				
			 j_terminalVersion = JSON.parse(AWI_getAPIVersion());
			 var terminalVersion = j_terminalVersion['version'];
			 var sappApiVersion = "<%=GlobalProperties.getProperty("Globals.apiVersion")%>";
			 if(versionCompare(terminalVersion, sappApiVersion) == -1) {
				alert('실행 가능한 Device버전이 아닙니다. Device의 버전을 업그레이드 하세요. 최소 요구 버전('+sappApiVersion+')');
				activeService = false;
				//AWI_closeServiceApp();
			 }
			 // **************************** 서비스 상태 체크 END
				
				
			 if(activeService) {

				 if(j_curWID=="") {
					 location.href="/svm/wallet/myWalletList";
				 } else {
					 $('#my_name').html(j_curANM);
					 $('#my_wallet_name').html(j_curWNM);
					 var myBalance = getBalance(j_curWID);
					 var dBalance = (parseInt(myBalance)/<%=CoinUtil.DISPLAY_COIN_UNIT%>);
					 $('#my_wallet_balance').html(gfnAddComma(dBalance) + " " + coinData['cou']);
				 }
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
			var sigRes = AWI_getSignature(pWalletId, sQuery, "query", "getBalance");
			if(sigRes['result']=="OK") {
				sSig = sigRes['signature_key'];
				
				// ************ step3 : get Balance / SVM API
				sQuery = {"npid":sNpid, "netType":j_curNetId,"chainID":j_chainID,"parameterArgs" : ["PID","10000",sNonce,sSig,pWalletId]};
				retData = WSI_callJsonAPI("/svm/wallet/getBalance", sQuery);
				if(retData['result'] == "OK") {
					rtnBalance = retData['balance'];
				} else {
					return '';
				}
			}
			return rtnBalance;
		}
		
	    // backup
	    function setBackUpFunc() {
			var pWalletId = j_curWID;			
			sReturn = AWI_setBackup(pWalletId);
			/* var joReturn = JSON.parse(sReturn);
			if (joReturn['result'] == 'OK') {
			} */			
		}
	    
	    // restore
	    function setRestoreFunc() {
			sReturn = AWI_setRestore();
			/* var joReturn = JSON.parse(sReturn);
			if (joReturn['result'] == 'OK') {
			} */		
		}
	    
	 	// showQRCode
	    function showQRCode() {
	    	var pWalletId = j_curWID;
	    	var pWalletName = j_curWNM;
	    	AWI_showQRCode(pWalletId, pWalletName);
	    	/* var joReturn = JSON.parse(sReturn);
			if (joReturn['result'] == 'OK') {
			} */
		}
	    
	 	// App To Web
	    function AWI_CallFromApp(strJson) {
	    	var joRoot = JSON.parse(strJson);  
	    	var joFunc = joRoot.func;
    		if(joFunc.cmd == 'backup') {
    			// '{ "func" : { "cmd" : "backup", "result" : "OK", "walletID" : "Wallet Id" }}'
        		//alert("backup complete");
    			//location.reload();
	    	}
    		
    		if(joFunc.cmd == 'restore') {
    			// '{ "func" : { "cmd" : "restore", "result" : "OK", "walletID" : "Wallet Id" }}'
    			if (joFunc.result == 'OK') {
	    			var pWalletId = joFunc.walletID;
	        		AWI_setAccountConfig("CUR_WID",pWalletId);
	        		location.href="/svm/wallet/createWalletRestore?wid="+pWalletId;
	    			//location.href="/index?wid="+pWalletId;
        		}
	    	}
	    }

		// Create Wallet
	    function createWallet() {
	    	location.href="/svm/wallet/createWalletForm";
		}
	    
		// My Wallet List
	    function myWalletList() {
	    	location.href="/svm/wallet/myWalletList";
		}

		// remittance
	    function sendCoin() {
	    	location.href="/svm/wallet/sendCoinForm";
		}	

		// Transaction history
	    function myTransHistory() {
	    	location.href="/svm/wallet/myTransHistory";
		}	
		
		// Add new service
	    function fnAddService() {
	    	location.href="/svm/service/addServiceForm";
		}	
		
	    function fnLogout() {
	    	AWI_logout();
		}
	    
	</script>
  </head>

<body>
    
    <style>
	#menu-box li {
		list-style:none;
		height:50px;
		line-height:50px;
		border-bottom:1px solid #646464;
	}
	#menu-box li a {
		font-size:12pt; 
		color:#fff; 
	}
	#menu-box li a i {
		background:#fff;
		padding:6px;
		color:#424242;
		border-radius:50%;
		width:30px;
		height:30px;
		margin-right:15px;
	}
    </style>
	<script>
	var onMenu = false;
	function onMainMenu() {
		onMenu = !onMenu;
		changeMainMenu(onMenu);
	}
	function changeMainMenu(chk) {
		onMenu = chk;
		if (chk) {
			$('#menu-box').show();
		} else {
			$('#menu-box').hide();
		}
	}
	function Fn_openLocation() {
		changeMainMenu(false);
		$('#locationDiv').show();
	}
	function Fn_actionLocation() {
		if($('#locationUrl').val()!="") {
			AWI_openAppByHost($('#locationUrl').val());
		}
	}
	</script>
	<div id="top_header" style="width:100%;height:60px;margin:0;background-color:#fff;border-bottom:1px solid #D0D0D0;z-index:900;">
		<div class="row">
			<div class="col-lg-8 col-md-8 col-sm-8 col-8" style="height:60px;line-height:60px;text-align:left;font-weight:bold;">
	            <a href="/index" style="padding-left:20px;color:#171E5E;">NODEHOME</a>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 col-4" style="height:60px;text-align:right;">
	            <div style="margin-top:15px;margin-right:27px;font-size:15pt;">
	                <i class="fas fa-ellipsis-v" onclick="onMainMenu();" style="color:#171E5E;"></i>
	            </div>
			</div>
		</div>
	</div>
	<div id="menu-box" style="position:absolute;display:none;left:0;top:61px;width:100%;background-color:#424242;padding:10px;color:#000;z-index:800;">
		<li onclick="javascript:Fn_openLocation();" style="color:#fff;"><i class="fas fa-location-arrow"></i> 호스트가기</li>
		<li onclick="javascript:fnLogout();" style="color:#fff;"><i class="fas fa-sign-out-alt"></i> 로그아웃</li>
		<li onclick="javascript:AWI_closeServiceApp();" style="color:#fff;"><i class="fas fa-times"></i> 서비스 종료</li>
		<div style="position:absolute;right:5px;bottom:-23px;font-size:20pt;background-color:#fff;height:23px;line-height:23px;"><i style="padding:0;margin:0;border:0;height:23px;line-height:23px;color:#414041;" class="fas fa-window-close" onclick="changeMainMenu(false)"></i></div>
	</div>
	<div id="locationDiv" style="display:none;border:1px solid #ccc;background:#fff;z-index:1200;height:150px;">
		<div class="col-xs-12 col-sm-12" style="height:40px;line-height:40px;margin-top:30px;">
			<input type="text" name="locationUrl" id="locationUrl" value="" placeholder="http://www.sample.com" class="form-control" />	
		</div> 
		<div class="col-xs-12 col-sm-12" style="height:60px;padding-top:15px;text-align:left;color:#127519;vertical-align: middle;line-height:60px;">
			<button type="button" class="btn btn-primary col-xs-12" onclick="Fn_actionLocation();">이동</button>
			<button type="button" class="btn btn-primary col-xs-12" onclick="$('#locationDiv').hide();">닫기</button>
		</div>
	</div>
	
  <!-- Content : S -->
  <div class="">     
    <div class="content">
      <div class="container-fluid">          
          <div class="row" style="margin-top:30px;">
            <div class="col-md-12">
              <div class="card card-profile">
                <div class="card-avatar" style="width: 130px; height: 130px; background-color: #2c3e50; padding-top: 50px; font-size: 30px; color: #ffffff; font-weight: bold;">
                  <span id="my_name"></span>
                  <a href="#pablo">
                    <!-- <img class="img" src="/bootstrap/assets/img/faces/profile.png" /> -->            
                  </a>
                </div>
                <div>
                  <!-- <h6 class="card-category text-gray"><span id="my_name"></span></h6> -->
                  <h5 class="card-title">
                  <spring:message code="wallet.text.walletname" /> : <span id="my_wallet_name"></span><br />
                  <spring:message code="wallet.text.balance" /> : <span id="my_wallet_balance"></span><br />
                  <button type="button" class="btn btn-primary btn-sm" onclick="javascript:showQRCode();"><spring:message code="user.text.qrzoom" /></button>
                  <button type="button" class="btn btn-primary btn-sm" onclick="javascript:setBackUpFunc();"><spring:message code="user.text.backup" /></button>
                  <button type="button" class="btn btn-primary btn-sm" onclick="javascript:setRestoreFunc();"><spring:message code="user.text.restore" /></button>
                  </h4>
                  <!-- <p class="card-description">
                   	Contents
                  </p>  -->
                </div>
              </div>
            </div>
          </div>
          
          <div class="row">            
            <div class="col-lg-3 col-md-3 col-sm-6 col-6">
              <div class="card card-stats" onclick="createWallet();">
                <div class="card-header card-header-success card-header-icon">
                  <div class="card-icon">
                    <i class="material-icons">&#xE02E;</i> <!-- library_add -->
                  </div>
                  <!-- <p class="card-category"><spring:message code="user.button.createWallet" /></p>
                  <h3 class="card-title">49/50
                    <small>GB</small>
                  </h3> -->
                </div>
                <div class="card-footer">
                  <div class="stats">
                   	<i class="material-icons">&#xE02E;</i> <spring:message code="user.button.createWallet" />
                  </div>
                </div>
              </div>
            </div>            
            <div class="col-lg-3 col-md-3 col-sm-6 col-6">
              <div class="card card-stats" onclick="myWalletList();">
                <div class="card-header card-header-warning card-header-icon">
                  <div class="card-icon">
                    <i class="material-icons">&#xE850;</i> <!-- account_balance_wallet -->
                  </div>
                  <!-- <p class="card-category"><spring:message code="user.button.selectWallet" /></p>
                  <h3 class="card-title">49/50
                    <small>GB</small>
                  </h3> -->
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <i class="material-icons">&#xE850;</i> <spring:message code="user.button.selectWallet" />
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-3 col-md-3 col-sm-6 col-6">
              <div class="card card-stats" onclick="sendCoin();">
                <div class="card-header card-header-danger card-header-icon">
                  <div class="card-icon">
                    <i class="material-icons">&#xE263;</i> <!-- monetization_on -->
                  </div>
                  <!-- <p class="card-category"><spring:message code="user.button.sendCoin" /></p>
                  <h3 class="card-title">49/50
                    <small>GB</small>
                  </h3> -->
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <i class="material-icons">&#xE263;</i> <spring:message code="user.button.sendCoin" />
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-3 col-md-3 col-sm-6 col-6">
              <div class="card card-stats" onclick="myTransHistory();">
                <div class="card-header card-header-info card-header-icon">
                  <div class="card-icon">
                    <i class="material-icons">&#xE889;</i> <!-- history -->
                  </div>
                  <!-- <p class="card-category"><spring:message code="user.button.transHistory" /></p>
                  <h3 class="card-title">49/50
                    <small>GB</small>
                  </h3> -->
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <i class="material-icons">&#xE889;</i> <spring:message code="user.button.transHistory" />
                  </div>
                </div>
              </div>
            </div>
            <!-- <div class="col-lg-3 col-md-3 col-sm-6 col-6">
              <div class="card card-stats" onclick="fnAddService();">
                <div class="card-header card-header-blue card-header-icon">
                  <div class="card-icon">
                    <i class="material-icons">&#xE02E;</i>
                  </div>
                </div>
                <div class="card-footer">
                  <div class="stats">
                    <i class="material-icons">&#xE02E;</i> 새 서비스 추가
                  </div>
                </div>
              </div>
            </div> -->
          </div>
          
          
      </div>
    </div>
  </div>
  <!-- Content : E -->
    

    <!--   Core JS Files   -->    
    <script src="/bootstrap/assets/js/core/jquery.min.js"></script>
    <script src="/bootstrap/assets/js/core/popper.min.js"></script>
    <script src="/bootstrap/assets/js/bootstrap-material-design.js"></script>

    <!--  Notifications Plugin, full documentation here: http://bootstrap-notify.remabledesigns.com/    -->
    <script src="/bootstrap/assets/js/plugins/bootstrap-notify.js"></script>

    <!--  Charts Plugin, full documentation here: https://gionkunz.github.io/chartist-js/ -->
    <script src="/bootstrap/assets/js/core/chartist.min.js"></script>

    <!-- Plugin for Scrollbar documentation here: https://github.com/utatti/perfect-scrollbar -->
    <script src="/bootstrap/assets/js/plugins/perfect-scrollbar.jquery.min.js"></script>

    <!-- Demo init -->
    <script src="/bootstrap/assets/js/plugins/demo.js"></script>

    <!-- Material Dashboard Core initialisations of plugins and Bootstrap Material Design Library -->
    <script src="/bootstrap/assets/js/material-dashboard.js?v=2.1.0"></script>
  
</body>
</html>