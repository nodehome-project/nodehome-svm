<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="io.nodehome.svm.common.util.EtcUtils"%>

<%
String localServiceHost = request.getRequestURL().toString();
localServiceHost = localServiceHost.replaceAll(request.getRequestURI(),"");
%>
<!DOCTYPE html>
<html>
	<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <title></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>    
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/css/style.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="/js/loader.js"></script>
    <script src="/js/cpwallet/app_interface.js"></script>
	<script>
		$(function() {
		});
		
		//This function is called as soon as it is loaded from the App.
		function AWI_OnLoadFromApp(dtype) {
			 AWI_ENABLE = true;
			 if(dtype=='android') {
		         AWI_DEVICE = dtype;
			 } else {
			     AWI_DEVICE = dtype;
			 }
		}
	</script>
    <script type="text/javascript">	
		function checkNull(string) {
			if (string==null || string=='') {
				return true;
			} else {
				return false;
			}
		}
	
		$(document).ready(function(){

			var loading = $('<div id="loading" class="loading"></div><img id="loading_img" alt="loading" src="/images/viewLoading.gif" />').appendTo(document.body).hide();
			$( document ).ajaxStart( function() {
				loading.show();
			} );
			$( document ).ajaxStop( function() {
				loading.hide();
			} );
			
		});
		
		// Wallet ID correction
		function updateWalletId() {
			$.post("/host/setupUpdateWid",$('#setform').serialize(),function(data,status){
			 	if(status == "success") {
					alert('Modifications completed');
			 	}
			});
		}
		
		function updateHosts() {
			if(setform.mport.value=="") {
				alert("Enter the host authentication port.");
				return;
			}
			$.post("/host/addHostProcess",$('#setform').serialize(),function(data,status){
			 	if(status == "success") {
					alert('Update completed');
					location.reload();
			 	}
			});
		}
    </script>
  	</head>
  <body>
  
		<nav class="navbar navbar-default navbar-fixed-top">
		  <div class="container">
		    <div class="navbar-header">
		      <a class="navbar-brand" style="font-size:17pt; color:#ffffff; font-family:Rockwell;" href="/user/login.jsp">NODEHOME Platform - Setup service</a>
		    </div>
		  </div>
		</nav>

	<div class="container">
		<p style="height:50px;">&nbsp;</p>
        <h1>Initial setting</h1> 
        
		<!-- Body : S -->
		<form name="setform" id="setform" method="post">
		<input type="hidden" name="serviceId" id="serviceId" value="${serviceId }"/>
		<input type="hidden" name="requestUrl" id="requestUrl" value="<%=localServiceHost %>"/>
		  <div id="isLogin">
			  <div class="form-group">
			    <label for="name">Host Wallet ID</label>
			    <input type="text" class="form-control" name="wid" id="wid" placeholder="Wallet ID" value="${walletId }"/>
			  </div>
		  
			  <div style="width:100%; text-align:center;">
			  	<button type="button" class="btn btn-primary btn-lg" style="width:100%; text-align:center;" onclick="updateWalletId();">Set Wallet ID</button>
			  </div>
			  <div style="width:100%; height:20px; text-align:center;"></div>
			  
			  
			  <div class="form-group">
			    <label for="name">Host List</label>
			    <%
			    String[] hostList = (String[])request.getAttribute("hostList");
	    		if(hostList!=null) {
	    			%>
				    <textarea name="" id="" style="color:#000;width:100%;height:100px;" class="form-control" readonly="readonly"><%
					   	for(int i=0; i<hostList.length; i++) { 
					   		if(hostList[i]!=null && !hostList[i].equals("")) out.print(hostList[i].trim()+"\r\n");
					   	}
					   	%>
				    </textarea>
	    			<%
	    		}
			    %>
			  </div>
		  
			  <div style="width:100%; text-align:center;" class="row">
			    <div class="col-sm-2" style="vertical-align:middle;height:32px;line-height:32px;text-align:right;">
			      Host domain
			    </div>
			    <div class="col-sm-4">
			      <input type="text" class="form-control" name="mdomain" id="mdomain" placeholder="http://domain.com" value=""/>
			    </div>
			    <div class="col-sm-2">
			       Host ip
			    </div>
			    <div class="col-sm-4">
			      <input type="text" class="form-control" name="mip" id="mip" placeholder="127.0.0.1" value=""/>
			    </div>
			    <p>&nbsp;</p>
			    
			    <div class="col-sm-2" style="vertical-align:middle;height:32px;line-height:32px;text-align:right;">
			      Host manager port
			    </div>
			    <div class="col-sm-2">
			      <input type="text" class="form-control" name="mport" id="mport" placeholder="8886" value="8886"/>
			    </div>
			    <div class="col-sm-8">
			      <button type="button" class="btn btn-primary btn-lg" style="width:100%; text-align:center;" onclick="updateHosts();">My Host Add Request and List Update</button>
			    </div>
			  </div>
			  <div style="width:100%; height:20px; text-align:center;"></div>
		  </div>
		</form> 
		<!-- Body : E -->
		
	</div>
	

  </body>

</html>

