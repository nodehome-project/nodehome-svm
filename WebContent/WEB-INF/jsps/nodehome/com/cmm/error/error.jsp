<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>

<script language="javascript">
function fncGoAfterErrorPage(){
    history.back(-2);
}
</script>
</head>
<body>
<div style="width: 1000px; margin: 50px auto 50px;">
	<div style="border: ppx solid #666; padding: 20px;">
		<p style="color:red; margin-bottom: 8px; ">Noname Error</p> 

		<div class="boxType1" style="width: 700px;">
			<div class="box">
				<div class="error">
					<p class="title">Noname Error</p>
					<p class="cont mb20">An unknown error has occurred.<br /></p>
					<span class="btn_style1 blue"><a href="javascript:fncGoAfterErrorPage();">Back page</a></span>
				</div>
			</div>
		</div>
	</div>
</div>

</body>
</html>
