<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
     
<script>
var sQuery = {"owner":"cMgcR49dvEfUrrp6gPSJFbe394SB2JNK2a9q4izbNj29jq8B5RLP","atchId":"15","atchSn": "1"};

$.ajax({
    url: "/svm/content/dataValidation", 
    type: 'POST',
    data: JSON.stringify(sQuery),
    dataType: 'json', 
	contentType:"application/json;charset=UTF-8",
    success: function(data) { 
		if(data['result'] != "FAIL") {
		} else {
			return false;
		}
    },
    error:function(data,status,er) { 
        alert("error: "+data.responseText+" status: "+status+" er:"+er);
    }
});

</script>

