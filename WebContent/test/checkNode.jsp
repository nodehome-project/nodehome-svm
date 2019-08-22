<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
     
    <meta charset="utf-8">
<script>
var sQuery = {"requestUrl" : "http://192.168.0.150:8080/checkNode"};
$.ajax({
    url: "/svm/common/callCorsUrl",
    type: 'POST',
    data: JSON.stringify(sQuery),
    dataType: 'json', 
	contentType:"application/json;charset=UTF-8",
    success: function(data) { 
    	alert(''+JSON.stringify(data));
    },
    error:function(data,status,er) { 
        alert("error: "+data.responseText+" status: "+status+" er:"+er);
    }
});

</script>

