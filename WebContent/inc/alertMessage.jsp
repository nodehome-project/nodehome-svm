<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
	<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <title></title>
  	<style>
  	.modal {
        width:93%;
        margin:0 auto;
        text-align: center;
        
	} 
	.modal-dialog {
        text-align: left;
        vertical-align: middle;
        padding-top:220px;
	}
  	</style>
  	</head>
<body>

<script>
function alertMassage(message) { // layer message window call
	$("#myModal").modal({backdrop: true}); //true:dark overlay, false:no overlay(transparent), static:no dark overlay close;
	$('#id_error_message').html(message); // Message output
}

function alertMassageCallBack(message) { // layer message window call
	$("#myModal").modal({backdrop: true}); //true:dark overlay, false:no overlay(transparent), static:no dark overlay close;
	$('#id_error_message').html(message); // Message output
	
	$("#myModal").on('hidden.bs.modal', function () { // Message output and history.back
		history.back();
	});
}
function alertMassageReload(message) { // layer message window call
	$("#myModal").modal({backdrop: true}); //true:dark overlay, false:no overlay(transparent), static:no dark overlay close;
	$('#id_error_message').html(message); // Message output
	
	$("#myModal").on('hidden.bs.modal', function () { // Message output and history.back
		location.reload();
	});
}

function alertMassageUrl(message,url) { // layer message window call
	$("#myModal").modal({backdrop: true}); //true:dark overlay, false:no overlay(transparent), static:no dark overlay close;
	$('#id_error_message').html(message); // Message output
	
	$("#myModal").on('hidden.bs.modal', function () { // Message output and history.back
		location.href=url;
	});
}
</script>
	<div class="container">
	
	  <!-- Modal -->
	  <div class="modal fade" id="myModal" role="dialog" style="z-index: 10000;">
	    <div class="modal-dialog">
	      <div class="modal-content" style="padding:20px 20px 20px 20px;">
	        <div style="height:36px; text-align:left;">
	          <span style="font-size:18px;" class="modal-title"></span>
	        </div>
	        <div style="height:34px; text-align:left; font-size:16px;" id='id_error_message'></div>
	        <div style="height:50px; text-align:right; vertical-align:bottom; padding-top:30px;">
	          <span data-dismiss="modal" style="color:#2faa9f;">Confirm</span>
	        </div>
	      </div>
	    </div>
	  </div>
	  <!-- Modal -->
	  
	</div>
	

  </body>

</html>
