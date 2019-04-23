<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="/js/tapp_interface.js"></script>
 
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.js"></script>

<script>
// Function to call as soon as it is loaded from the App
function AWI_OnLoadFromApp(dtype) {
	 // Activate AWI_XXX method
	 AWI_ENABLE = true;
	 AWI_DEVICE = dtype;

	 chk = AWI_isCheckedPassword();
	 if(chk=="OK") {
		 location.href="/helloworld.jsp";
	 } else {
		$.alert({
			    title: '안내',
			    content: "로그인을 하세요.",
			    confirm: function(){
			    },
			    onClose: function(){
			    	AWI_closeServiceApp();
			    },
		});
	 }
}
</script>