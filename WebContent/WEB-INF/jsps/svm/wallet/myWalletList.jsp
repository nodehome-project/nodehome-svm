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
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="java.text.*" %>
<%@ page import="java.util.List" %>

<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

// WID
String strAWID = request.getParameter("wid");
if (strAWID == null) strAWID = "";
String projectServiceid = GlobalProperties.getProperty("project_serviceID");
%>
<!DOCTYPE html>
<html>
  <head>
    <title></title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

    <script src="/bootstrap/assets/js/core/jquery.min.js"></script>
    
    <!--     Fonts and icons     -->
    <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />

    <!-- Material Dashboard CSS -->
    <link rel="stylesheet" href="/bootstrap/assets/css/material-dashboard.css">
    
    <!-- CSS Just for demo purpose, don't include it in your project -->
    <link href="/bootstrap/assets/demo/demo.css" rel="stylesheet" />
    
    <link href="/css/loading.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.1/css/all.css" integrity="sha384-5sAR7xN1Nv6T6+dT2mhtzEpVJvfS3NScPQTrOxhwjIuvcA67KV2R5Jz6kr4abQsz" crossorigin="anonymous">
    
    <!-- Platform JS -->
    <script src="/js/loader.js"></script>
    <script src="/js/common.js"></script>
    <script src="/js/tapp_interface.js"></script>
    
    <script>
	$.ajaxSetup({ async:false }); // AJAX calls in order

	var coinData;
	var policyData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
	// Script to run as soon as loaded from the web
	$(function() {
	});

	/* function AWI_OnBackPressedFromApp() {
		location.href="/index";
	} */
	
	var walletList = null;
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
		$('#loading').css('display','block');

		setTimeout(function () {
			 // get wallet list
			 var joReturn = AWI_getWalletList();
			 var sResult = JSON.parse(joReturn);
			 var rowSelIdx = -1;
			 if(sResult['result']=="OK" && sResult['list']!="" && sResult['list'].length>0) {
				walletList = sResult['list'];
		    	var sHTML="";
		    	var elmtTBody = $('#id_walletlist_table_tbody');
		    	for(var i=0; i < walletList.length ; i ++) {
		    		var vBalance = getBalance(walletList[i]['walletID']);
		    		if(vBalance!="") {
			    		var wallerNm = AWI_getAccountConfig(walletList[i]['walletID']);	
			    		var walletRegisted = false;
			    		var configChk = AWI_getAccountConfig("REG_"+walletList[i]['walletID']);
			    		if(configChk=="") {
				    		var data = WSI_callJsonAPI("/svm/wallet/queryWalletInfo", {"netType":j_curNetId,"chainID":j_chainID,"serviceID":"<%=GlobalProperties.getProperty("project_serviceID")%>", "walletID":walletList[i]['walletID']} );
				    		if(data.result=="OK") {
				    			walletRegisted = true;
				    			AWI_setAccountConfig("REG_"+walletList[i]['walletID'],"Y");	// Whether to register in the chain
				    		} else {
				    			AWI_setAccountConfig("REG_"+walletList[i]['walletID'],"N");	// Whether to register in the chain
				    		}
			    		} else {
			    			if(configChk=="Y") walletRegisted = true;
			    		}
			    		sHTML += '<tr id="id_WalletID_' + i + '" style="background-color:#ffffff;">';
			    		sHTML += '<td>';
			    		sHTML += '	<div class="form-radio">';
			    		sHTML += '		<label class="form-radio-label">';
			    		sHTML += '		<input class="form-radio-input" type="radio" name="name_WalletID" value="' + walletList[i]['walletID'] + '"/>';
			    		sHTML += '		<input type="hidden" name="name_WalletNM" value="' + wallerNm + '"/>';
			    		sHTML += '		<input type="hidden" name="walletRegisted" value="' + walletRegisted + '"/>';
			    		sHTML += '		<input type="hidden" name="walletBalance" value="' + vBalance + '"/>';
			    		sHTML += '		<span class="form-radio-sign">';
			    		sHTML += '		<span class="radio"></span>';
			    		sHTML += '		</span>';
			    		sHTML += '		</label>';
			    		sHTML += '	</div>';
			    		sHTML += '</td>';
			    		sHTML += '<td>' + wallerNm + '</td>';
			    		sHTML += '<td style="word-break: break-all;"><span style="font-size:16px; font-weight:bold; color:#f58903;">' + gfnAddComma(parseInt( vBalance )/<%=CoinUtil.DISPLAY_COIN_UNIT%>) + '</span> <span style="font-size:14px;" class="aaa"> '+coinData['cou']+'</span></td>';
			    		sHTML += '<td>';
			    		if(walletRegisted)
			    			sHTML += '<i class="fas fa-infinity"></i>';
			    		sHTML += '</td>';
			    		sHTML += '</tr>';
		   	  			if (j_curWID == walletList[i]['walletID']) {
		   					rowSelIdx = i;
		   			    }
		    		}
		    	}
		    	elmtTBody.html(sHTML);
		    	if(frm.name_WalletID.length===undefined || frm.name_WalletID.length==1) {
		    		if(rowSelIdx==0)
		    			frm.name_WalletID.checked=true;
		    	} else {
			    	if(rowSelIdx!=-1) {
			    		frm.name_WalletID[rowSelIdx].checked=true;
			    	}
		    	}
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

	// Delete wallet
	function FnDeleteWallet() {
		var selId = "";
		if(frm.name_WalletID && frm.name_WalletID.length>0) {
			for(var i=0; i<frm.name_WalletID.length; i++) {
				if(frm.name_WalletID[i].checked) {
					selId = frm.name_WalletID[i].value;
					break;
				}
			}
		}
		if(selId=="") {
			if(frm.name_WalletID.checked) {
				selId = frm.name_WalletID.value;
			}
		}
		if(selId!="") {
			if(confirm('<spring:message code="wallet.msg.delwallet" />')) {
				var sReturn = AWI_deleteWallet(selId);
				var joReturn = JSON.parse(sReturn);
				if (joReturn['result'] == 'OK') {
					location.reload();
				}	
			}	
		} else {
			alert('<spring:message code="body.msg.select.please.wallet" />');
		}
	}

	function FnSelectWallet() {
		var selId = "";
		var selNm = "";
		if(frm.name_WalletID && frm.name_WalletID.length>0) {
			for(var i=0; i<frm.name_WalletID.length; i++) {
				if(frm.name_WalletID[i].checked) {
					selId = frm.name_WalletID[i].value;
					selNm = frm.name_WalletNM[i].value;
					break;
				}
			}
		}
		if(selId=="") {
			if(frm.name_WalletID.checked) {
				selId = frm.name_WalletID.value;
				selNm = frm.name_WalletNM.value;
			}
		}
		if(selId!="") {
			AWI_setAccountConfig("CUR_WID",selId);
			location.href = "/index?wid="+selId;
			return;
		} else {
			alert('<spring:message code="body.msg.select.please.wallet" />');
		}
	}

	// 체인에 등록/수정
	function FnAddWallet() {
		var selId = "";
		var selNm = "";
		var walletRegisted = "";
		var iWalletBalance = 0;

		var regFee = parseInt(policyData['FeeRegisterWallet']);
		if(frm.name_WalletID && frm.name_WalletID.length>0) {
			for(var i=0; i<frm.name_WalletID.length; i++) {
				if(frm.name_WalletID[i].checked) {
					selId = frm.name_WalletID[i].value;
					selNm = frm.name_WalletNM[i].value;
					walletRegisted = frm.walletRegisted[i].value;
					iWalletBalance = frm.walletBalance[i].value;
					break;
				}
			}
		}
		if(selId=="") {
			if(frm.name_WalletID.checked) {
				selId = frm.name_WalletID.value;
				selNm = frm.name_WalletNM.value;
				walletRegisted = frm.walletRegisted.value;
				iWalletBalance = frm.walletBalance.value;
			}
			if(selId=="") {
				alert('<spring:message code="body.msg.select.please.wallet" />');
				return;
			}
		}
		if(regFee > iWalletBalance) {
			alert('<spring:message code="wallet.msg.addwallet.fail3" /> <spring:message code="body.text.wallet.regist.fee" />'+ (policyData['FeeRegisterWallet'] / parseInt('<%=CoinUtil.DISPLAY_COIN_UNIT%>')) +' '+coinData['cou']);
			return;
		}
		if(selId!="") {
			location.href="/svm/wallet/registWalletForm?sWid="+selId+"&maxRemittance="+coinData['mxt'];			
		} else {
			alert('지갑을 선택하세요.');
		}
	}

	function FnBlockUseWallet() {
		var selId = "";
		var selNm = "";
		var walletRegisted = "";
		var iWalletBalance = 0;
		var regFee = policyData['FeeRegisterWallet'];
		if(frm.name_WalletID && frm.name_WalletID.length>0) {
			for(var i=0; i<frm.name_WalletID.length; i++) {
				if(frm.name_WalletID[i].checked) {
					selId = frm.name_WalletID[i].value;
					selNm = frm.name_WalletNM[i].value;
					walletRegisted = frm.walletRegisted[i].value;
					iWalletBalance = frm.walletBalance[i].value;
					break;
				}
			}
		}
		if(selId=="") {
			if(frm.name_WalletID.checked) {
				selId = frm.name_WalletID.value;
				selNm = frm.name_WalletNM.value;
				walletRegisted = frm.walletRegisted.value;
				iWalletBalance = frm.walletBalance.value;
			}
		}
		
		var minRemitt = coinData['mnt'];
		if(minRemitt <= iWalletBalance) {
			alert('<spring:message code="wallet.msg.stop.wallet.confirm2" />'+minRemitt+' '+coinData['cou']);
			return;
		}
		
		var confm = confirm('<spring:message code="wallet.msg.stop.wallet.confirm1" />');
		if(confm) {
			if(selId!="") {
				$('#loading').css('display','block');

				setTimeout(function () {
					// ************ step1 : get Nonce / SVM API
					var sQuery = {"pid":"PID", "ver":"10000", "serviceID":"<%=projectServiceid%>","netType":j_curNetId,"chainID":j_chainID};
					var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
					if(retData['result'] == "OK") {
						sNonce = retData['nonce'];
						sNpid = retData['npid'];
					} else {
						$('#loading').css('display','none');
						return false;
					}

					// ************ step2 : get Signature / S-T API
					sQuery = ["PID","10000",sNonce];
					var sigRes = AWI_getSignature(selId, sQuery,"invoke","registerBlacklistUser");

					if(sigRes['result']=="OK") {
						sSig = sigRes['signature_key'];	

						// ************ step3 : SVM API
						var sArgs = ["PID","10000",sNonce,sSig,selId];

						sQuery = {"npid":sNpid, "serviceID":"<%=projectServiceid%>", "parameterArgs" : sArgs, "netType":j_curNetId,"chainID":j_chainID};
						retData = WSI_callJsonAPI("/svm/wallet/registerBlacklistUser", sQuery);

						if (retData['ec'] == "0") {
							AWI_deleteWallet(selId);
							location.reload();
						} else {
							alert(retData['ref']);
							$('#loading').css('display','none');
						}
					}
				},10);
			} else {
				alert('<spring:message code="body.msg.select.please.wallet" />');
			}
		}
	}
	</script>
  </head>

<body>
    
  <!-- Content : S -->
  <div class="wrapper ">   
  	<div id="loading" class="loading" style="display:none;"><img id="loading_img" alt="loading" src="/images/viewLoading.gif" /></div>
  	  
    <div class="content">
      <div class="container-fluid">
          
          <form name="frm" id="frm" method="post" onsubmit="return false;" class="form-horizontal">
		  <input type="hidden" name="nonce" id="nonce" />
          <input type="hidden" name="npid" id="npid" />
		        
          <div class="row">
            <div class="col-lg-12 col-md-12">
              <div class="card">
                <div class="card-header card-header-warning">
                  <h4 class="card-title"><spring:message code="body.text.mywallet.list" /></h4>
                  <p class="card-category">Wallet List</p>
                </div>
                <div class="card-body table-responsive">
                  <table class="table table-hover">
                    <thead class="text-warning">
                      <th></th>
                      <th>Name</th>
                      <th>Balance</th>
                      <th></th>
                    </thead>                    
                    <tbody id='id_walletlist_table_tbody'>
					</tbody>					
                  </table>
                  <spring:message code="title.description" /> : <i class="fas fa-infinity"></i> <spring:message code="body.text.regist.blockchain.check" />
                </div>
              </div>
            </div>
          </div>
          
			<hr class="colorgraph">
			<div class="row">
				<div class="col-12"><input type="button" value="지갑 사용선택" class="btn btn-success  btn-block" onclick="FnSelectWallet();" /></div>
			</div>
			<div class="row">
				<div class="col-6"><input type="button" value="Device에서 제거" class="btn btn-warning  btn-block" onclick="FnDeleteWallet();" /></div>
				<div class="col-6"><input type="button" value="체인에 등록/수정" class="btn btn-warning  btn-block" onclick="FnAddWallet();" /></div>
			</div>
			<div class="row">
				<div class="col-12"><input type="button" value="지갑 사용 중지 등록" class="btn btn-warning  btn-block" onclick="FnBlockUseWallet();" /></div>
			</div>
			<div style="padding-bottom:20px;"></div>
				
          </form>
          
          
      </div>
    </div>
  </div>
  <!-- Content : E -->
    

    <!--   Core JS Files   -->
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