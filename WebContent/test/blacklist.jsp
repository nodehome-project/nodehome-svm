<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
     
<script>
var sQuery = {"serviceID" : "nodehome", "verifiHost":"http://nodehome1.nodehome.io", "mport":"8886"};
$.ajax({
    url: "/host/checkBlacklist", 
    type: 'POST',
    data: JSON.stringify(sQuery),
    dataType: 'json', 
	contentType:"application/json;charset=UTF-8",
    success: function(data) { 
		if(data['result'] != "FAIL") {
			alert(JSON.stringify(data));
		} else {
			alert(data['result']);
			return false;
		}
    },
    error:function(data,status,er) { 
        alert("error: "+data.responseText+" status: "+status+" er:"+er);
    }
});
</script>
