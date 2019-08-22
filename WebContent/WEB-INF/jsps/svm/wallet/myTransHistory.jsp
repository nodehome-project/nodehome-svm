<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO" %>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.util.DateUtil"%>
<%@ page import="io.nodehome.cmm.FouriMessageSource"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
<%@ page import="io.nodehome.svm.common.CoinUtil"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>

<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

// WID
String strAWID = request.getParameter("wid");
if (strAWID == null) strAWID = "";


String today = DateUtil.getDate("yyyy-MM-dd");
String today1W = DateUtil.getFormatDate("yyyy-MM-dd", "D", -7);
String today1M = DateUtil.getFormatDate("yyyy-MM-dd", "M", -1);
String today3M = DateUtil.getFormatDate("yyyy-MM-dd", "M", -3);

String sdate = request.getParameter("sdate");
String edate = request.getParameter("edate");
if(sdate==null) sdate=today;
if(edate==null) edate=today;
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
    
    <!-- CSS Just for demo purpose, don't include it in your project -->
    <link href="/bootstrap/assets/demo/demo.css" rel="stylesheet" />
    
    <!-- Platform JS -->
    <script src="/js/loader.js"></script>
    <script src="/js/common.js"></script>
    <script src="/js/tapp_interface.js"></script>
    
    <script>
	$.ajaxSetup({ async:false }); // AJAX calls in order

	var coinData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
	// Script to run as soon as loaded from the web
	$(function() {
	});

	var pageIndex = 1; // page number
	var searchIndexNo = 0; // search record start number
	var maxPageNo = 0; // maximum page number
	var listCount = 10; // Number of records (search)
	
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
		 
		 j_curWID = AWI_getAccountConfig("CUR_WID");
		 j_curWNM = AWI_getAccountConfig(j_curWID);

		 $('#wid').val(j_curWID);
		 document.getElementById("s_wallet_name").innerHTML = j_curWNM;
		 
		 FnLoadHistory();
	}

	// Search
	function submitSearch() {
		pageIndex = 1; // page number
		searchIndexNo = 0; // search record start number
		
		$('#id_walletlist_table_tbody').html("");
		var rtnHis = getSearchHistory(j_curWID);
		var listCnt = rtnHis['total'];
		maxPageNo = Math.ceil(listCnt/listCount);
		displayList(rtnHis['Record']);
		if(maxPageNo > pageIndex) {
			$('#more_btn').css('display','block');
		} else {
			$('#more_btn').css('display','none');
		} 
	}
	
	// more Search
	function moreSearch() {
		pageIndex ++; // page number
		searchIndexNo = (pageIndex-1) * listCount; // search record start number
		
		var rtnHis = getSearchHistory(j_curWID);
		var listCnt = rtnHis['total'];
		maxPageNo = Math.ceil(listCnt/listCount);
		displayList(rtnHis['Record']);

		if(maxPageNo > pageIndex) {
			$('#more_btn').css('display','block');
		} else {
			$('#more_btn').css('display','none');
		} 
	}
	
	// Date setting
	function setSearchDate(pGubun) {
		if(pGubun==1) {
			userWalletHistoryFom.sdate.value = "<%=today%>";
			userWalletHistoryFom.edate.value = "<%=today%>";
		} else if(pGubun==2) { 
			userWalletHistoryFom.sdate.value = "<%=today1W%>";
			userWalletHistoryFom.edate.value = "<%=today%>";
		} else if(pGubun==3) { 
			userWalletHistoryFom.sdate.value = "<%=today1M%>";
			userWalletHistoryFom.edate.value = "<%=today%>";
		} else if(pGubun==4) { 
			userWalletHistoryFom.sdate.value = "<%=today3M%>";
			userWalletHistoryFom.edate.value = "<%=today%>";
		}
	}
	</script>

	<script>
	
	function FnLoadHistory() {
		var rtnHis = getNewHistory(j_curWID);
		var listCnt = rtnHis['total'];
		maxPageNo = Math.ceil(listCnt/listCount);
		displayList(rtnHis['Record']);
	}
	
	function displayList(j_jaHistroy) {
		var sHtml = "";
		for(var i = 0; i < j_jaHistroy.length ; i ++) {
			sHtml +="<tr><td style=\"text-align:left; padding-top: 13px; padding-left:10px; line-height:22px;\">";
			sHtml +="<span style=\"font-size:12px;\">"+ unixTimeToDateTime(j_jaHistroy[i]['ttm']) +"</span><br />";
			if(j_jaHistroy[i]['mno']==null || j_jaHistroy[i]['mno']=='')
				sHtml +="<span style=\"font-size:12px;\">"+j_jaHistroy[i]['msg']+"</span><br />";
			else 
				sHtml +="<span style=\"font-size:12px;\">"+j_jaHistroy[i]['mno']+"</span><br />";
			sHtml +="</td>";
			sHtml +="<td style=\"text-align:right; padding-top: 13px; padding-left:10px; line-height:22px;\">";
			if(gfnAddComma(j_jaHistroy[i]['flg'])=='1') {
				sHtml +="<span style=\"font-size:16px; font-weight:bold; color:#d34202;\">"+gfnAddComma(j_jaHistroy[i]['val']/<%=CoinUtil.DISPLAY_COIN_UNIT%>)+"</span> <span style=\"font-size:14px;\" class=\"aaa\"> "+coinData['cou']+"</span><br />";
			} else {
				sHtml +="<span style=\"font-size:16px; font-weight:bold; color:blue;\">"+gfnAddComma(j_jaHistroy[i]['val']/<%=CoinUtil.DISPLAY_COIN_UNIT%>)+"</span> <span style=\"font-size:14px;\" class=\"aaa\"> "+coinData['cou']+"</span><br />";
			}
			sHtml +="</td>";
			sHtml +="</tr>"; 
		}
		$('#id_walletlist_table_tbody').append(sHtml);
	}

	// Recent transaction history
	function getNewHistory(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"PID", "ver":"10000", "nType":"query","netType":j_curNetId,"chainID":j_chainID};
		var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
		if(retData['result'] == "OK") {
			sNonce = retData['nonce'];
			sNpid = retData['npid'];
		} else {
			return false;
		}
		var stdt = userWalletHistoryFom.sdate.value;
		var eddt = userWalletHistoryFom.edate.value;
		stdt = (getTimestampFromDatetime(stdt+" 00:00") / 1000) + "";
		eddt = (getTimestampFromDatetime(eddt+" 23:59") / 1000) + "";
		
		// ************ step2 : get Signature / S-T API
		sQuery = ["PID","10000",stdt,eddt,"Y",((pageIndex-1)*listCount)+"",listCount+"",sNonce];
		var sigRes = AWI_getSignature(pWalletId, sQuery,"query","getTransHistory");
		if(sigRes['result']=="OK") {
			sSig = sigRes['signature_key'];
			
			// ************ step3 : get TransHistory / SVM API
			sQuery = {"npid":sNpid, "parameterArgs" : ["PID","10000",stdt,eddt,"Y",((pageIndex-1)*listCount)+"",listCount+"",sNonce,sSig,pWalletId],"netType":j_curNetId,"chainID":j_chainID};
			retData = WSI_callJsonAPI("/svm/wallet/getTransHistory", sQuery);		// Transaction history API call
			if(retData['result'] == "OK") {
				return retData['value'];
			} else {
				return '';
			}
		}
		return '';
	}

	// View search list
	function getSearchHistory(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"PID", "ver":"10000", "nType":"query","netType":j_curNetId,"chainID":j_chainID};
		var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
		if(retData['result'] == "OK") {
			sNonce = retData['nonce'];
			sNpid = retData['npid'];
		} else {
			return false;
		}

		var stdt = userWalletHistoryFom.sdate.value;
		var eddt = userWalletHistoryFom.edate.value;
		stdt = (getTimestampFromDatetime(stdt+" 00:00") / 1000) + "";
		eddt = (getTimestampFromDatetime(eddt+" 23:59") / 1000) + "";

		// ************ step2 : get Signature / S-T API
		sQuery = ["PID","10000",stdt,eddt,"Y",((pageIndex-1)*listCount)+"",listCount+"",sNonce];
		var sigRes = AWI_getSignature(pWalletId, sQuery,"query","getTransHistory");
		if(sigRes['result']=="OK") {
			sSig = sigRes['signature_key'];	
			
			// ************ step3 : get TransHistory / SVM API
			sQuery = {"npid":sNpid, "parameterArgs" : ["PID","10000",stdt,eddt,"Y",((pageIndex-1)*listCount)+"",listCount+"",sNonce,sSig,pWalletId],"netType":j_curNetId,"chainID":j_chainID};
			retData = WSI_callJsonAPI("/svm/wallet/getTransHistory", sQuery);		// Transaction history API call
			if(retData['result'] == "OK") {
				return retData['value'];
			} else {
				return '';
			}
		}
		return '';
	}
	
	function FnMainGo() {
		location.href = "/index?wid="+j_curWID;
	}
	</script>
  </head>

<body>
    
  <!-- Content : S -->
  <div class="wrapper ">     
    <div class="content">
      <div class="container-fluid">
          
          <form name="userWalletHistoryFom" method="post" action="WalletHistory.jsp" onsubmit="return false;">
		  <input type="hidden" id="wid" name="wid" value=""/>
		        
          <div class="row">
            <div class="col-lg-12 col-md-12">
              <div class="card">
                <div class="card-header card-header-info">
                  <h4 class="card-title"><spring:message code="body.text.mytrans.list" /></h4>
                  <p class="card-category">Transaction history</p>
                </div>
                <div class="card-body table-responsive">
                  
                  
				<table style="width:100%; text-align: center; border:1px solid #ffffff;">
					<thead>
						<tr>
						<th width="50%" style="text-align:left; padding-top:10px; padding-left:10px;"><span class="input-group-addon" style="font-weight:bold; height:50px;" id="s_wallet_name"></span> <spring:message code="user.button.transHistory" /></th>
						<td width="50%" style="text-align:right; padding-top:10px; padding-right:10px;"><spring:message code="body.text.date.term" /></td>
						</tr>
					</thead>
					<tbody>								
						<tr>
							<td colspan="2" style="text-align:center; vertical-align:top;">
								<div id="search_date_zone">
								<span class="btn btn-warning btn-sm" style="width:23%;" onclick="setSearchDate(1)"><spring:message code="body.text.day" /></span>
								<span class="btn btn-warning btn-sm" style="width:23%;" onclick="setSearchDate(2)"><spring:message code="body.text.one.week" /></span>
								<span class="btn btn-warning btn-sm" style="width:23%;" onclick="setSearchDate(3)"><spring:message code="body.text.one.month" /></span>
								<span class="btn btn-warning btn-sm" style="width:23%;" onclick="setSearchDate(4)"><spring:message code="body.text.three.month" /></span>
								</div>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;vertical-align:middle; height:40px; padding-top:5px;">
								<div class="btn-group" role="group">
		    						<input type="text" class="form-control" placeholder="<spring:message code="body.text.start.date" />" name="sdate" maxlength="20" style="width:100px; height:35px;" value="<%=sdate%>"/>
								</div>
								&nbsp; - &nbsp;
                    			<div class="btn-group" role="group">
		    						<input type="text" class="form-control" placeholder="<spring:message code="body.text.end.date" />" name="edate" maxlength="20" style="width:100px; height:35px;" value="<%=edate%>"/>
								</div>					
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align:center;vertical-align:middle;height:40px; padding:7px;">
								<input type="submit" class="btn btn-success btn-lg" style="width:100%;" value="<spring:message code="body.text.view" />" onclick="submitSearch()"/>
							</td>
						</tr>
					</tbody>
				</table>
                  
                  
                  
                  
                  <table class="table table-hover">
                    <thead class="text-warning" style="text-align:center;">
                      <th>Date/Text</th>
                      <th>Balance</th>
                    </thead>                    
                    <tbody id='id_walletlist_table_tbody'>
                    </tbody>					
                  </table>
                  
                  <p id="more_btn" style="display:none; padding:7px;"><input type="submit" class="btn btn-primary btn-default btn-block" value="More" onclick="moreSearch()" /></p>
                  
                </div>
              </div>
            </div>
          </div>			
		
		  <div class="form-group" style="text-align:center;">
	          <div class="col-xs-12"><input type="button" value="<spring:message code="body.text.goback" />" class="btn btn-info btn-lg btn-block" onclick="FnMainGo();" /></div>
		  </div>
          
          </form>
          
          
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