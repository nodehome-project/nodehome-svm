<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
	<head>
    <title></title>
    <!-- Required meta tags -->
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="viewport" content="width=device-width">
    <!-- <meta content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;" name="viewport" /> -->    
    
    <!-- bootstrap 3.3.7 -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="/js/tapp_interface.js"></script>
	<script>
		var j_curANM;
		var j_curNetId;
		
		// Function to call as soon as it is loaded from the App
		function AWI_OnLoadFromApp(dtype) {
			 // Activate AWI_XXX method
			 AWI_ENABLE = true;
	         AWI_DEVICE = dtype;
	    	 chk = AWI_isCheckedPassword();
	    	 if(chk=="OK") {
	    		 location.href="/index";
	    	 } else {
	    		$.alert({
	    			    title: '안내',
	    			    content: "로그인을 하세요.",
	    			    confirm: function(){
	    			    },
	    			    onClose: function(){
	    			    	AWI_logout();
	    			    },
	    		});
	    	 }
		}
    </script>
    
  	</head>
  
  
<body>
	
</body>

</html>
