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

<%@ include file="/inc/alertMessage.jsp" %>
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

	var pageIndex = 1; // page number
	var searchIndexNo = 0; // search record start number
	var maxPageNo = 0; // maximum page number
	var listCount = 10; // Number of records (search)
	
	var coinData;
	var policyData;
	var j_curNetId;
	var j_chainID;
	var j_seedHost;
	
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
		displayList(rtnHis['record']);
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
		displayList(rtnHis['record']);

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
	
	function FnLoadHistory() {
		var rtnHis = getSearchHistory(j_curWID);
		var listCnt = rtnHis['total'];
		maxPageNo = Math.ceil(listCnt/listCount);
		displayList(rtnHis['record']);
	}
	
	function displayList(j_jaHistroy) {
		var sHtml = "";
		for(var i = 0; i < j_jaHistroy.length ; i ++) {
			sHtml +="<tr><td style=\"text-align:left; padding-top: 13px; padding-left:10px; line-height:22px;\">";
			sHtml +="<span style=\"font-size:12px;\">"+ unixTimeToDateTime(j_jaHistroy[i]['timestamp']) +"</span><br />";
			sHtml +="<span style=\"font-size:12px;\">"+j_jaHistroy[i]['memo']+"</span><br />";
			sHtml +="</td>";
			sHtml +="<td style=\"text-align:right; padding-top: 13px; padding-left:10px; line-height:22px;\">";
			if(gfnAddComma(j_jaHistroy[i]['type'])=='1') {
				sHtml +="<span style=\"font-size:16px; font-weight:bold; color:#d34202;\">-"+gfnAddComma(j_jaHistroy[i]['amount'])+"</span> <span style=\"font-size:14px;\" class=\"aaa\"></span><br />";
			} else {
				sHtml +="<span style=\"font-size:16px; font-weight:bold; color:blue;\">+"+gfnAddComma(j_jaHistroy[i]['amount'])+"</span> <span style=\"font-size:14px;\" class=\"aaa\"></span><br />";
			}
			sHtml +="</td>";
			sHtml +="</tr>";
		}
		$('#id_walletlist_table_tbody').append(sHtml);
	}

	// View search list
	function getSearchHistory(pWalletId) {
		var sNonce = "";	// nonce string
		var sSig = "";		// signature string
		var sNpid = "";		// NA connect id
		
		var stdt = userWalletHistoryFom.sdate.value;
		var eddt = userWalletHistoryFom.edate.value;
		stdt = (getTimestampFromDatetime(stdt+" 00:00") / 1000) + "";
		eddt = (getTimestampFromDatetime(eddt+" 23:59") / 1000) + "";

		sQuery = {"npid":sNpid, "parameterArgs" : ["PID","10000",pWalletId,stdt,eddt,"Y",((pageIndex-1)*listCount)+"",listCount+""], "netType":j_curNetId,"chainID":j_chainID};
		retData = WSI_callJsonAPI("/svm/token/queryTokenTransHistory", sQuery);		// Transaction history API call
		if(retData['result'] == "OK") {
			return retData['value'];
		}
		return '{}';
	}
	
	function FnMainGo() {
		location.href = "/index?wid="+j_curWID;
	}
	</script>
  </head>

<body>
    
  <!-- Content : S -->
  <div class="container ">     
          
          <form name="userWalletHistoryFom" method="post" action="WalletHistory.jsp" onsubmit="return false;">
		  <input type="hidden" id="wid" name="wid" value=""/>
		        
          <div class="row">
            <div class="col-lg-12 col-md-12">
              
              	<h2><spring:message code="body.text.mytrans.list" /> <br /><small>Transaction history</small></h2>
              	
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
		    						<input type="text" class="form-control" placeholder="<spring:message code="body.text.start.date" />" name="sdate" maxlength="20" style="width:130px; height:35px;" value="<%=sdate%>"/>
								</div>
								&nbsp; - &nbsp;
                    			<div class="btn-group" role="group">
		    						<input type="text" class="form-control" placeholder="<spring:message code="body.text.end.date" />" name="edate" maxlength="20" style="width:130px; height:35px;" value="<%=edate%>"/>
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

			<div style="" class="row">
				<div class="col-12" style=""><div style="padding:2px 5px 2px 5px;background-color:#4A76D9;border-radius:15px;color:#fff;text-align:center;" onclick="FnMainGo();"><spring:message code="body.text.goback" /></div></div>
			</div>		
          
          </form>
          
          
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