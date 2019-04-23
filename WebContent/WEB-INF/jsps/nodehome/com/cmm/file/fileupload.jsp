<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<script type = "text/javascript" src = "/ckeditor/ckeditor.js"></script>
 
<script type = "text/javascript">
	window.parent.CKEDITOR.tools.callFunction('${CKEditorFuncNum}','${filePath}', 'Upload Complete');
</script>
