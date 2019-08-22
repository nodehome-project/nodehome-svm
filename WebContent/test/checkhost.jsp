<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
     
<script>
var sQuery = {"mport":"8886","serviceID":"nodehome","requestUrl":"http://nodehome1.nodehome.io","targetUrl":"http://nodehome2.nodehome.io"};
$.ajax({
    url: "/eventCheckNode", 
    type: 'POST',
    data: JSON.stringify(sQuery),
    dataType: 'json', 
	contentType:"application/json;charset=UTF-8",
    success: function(data) { 
    	alert(' : '+JSON.stringify(data));
    },
    error:function(data,status,er) { 
        alert("error: "+data.responseText+" status: "+status+" er:"+er);
    }
});

</script>

