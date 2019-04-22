<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript" language="javascript">
	function fn_egov_downFile(atchFileId, fileSn) {
		window.open("<c:url value='/common/file/file_down.do?atchFileId="+atchFileId+"&fileSn="+fileSn+"'/>");
	}	
	
	function fn_egov_deleteFile(atchFileId, fileSn) {
		forms = document.getElementsByTagName("form");

		for (var i = 0; i < forms.length; i++) {
			if (typeof(forms[i].atchFileId) != "undefined" &&
					typeof(forms[i].fileSn) != "undefined" &&
					typeof(forms[i].fileListCnt) != "undefined") {
				form = forms[i];
			}
		}
		form.atchFileId.value = atchFileId;
		form.fileSn.value = fileSn;
		form.action = "<c:url value='/common/file/delete_file_info.do'/>";
		form.target="_self";
		form.submit();
	}
	
	function fn_egov_check_file(flag) {
		if (flag=="Y") {
			document.getElementById('file_upload_posbl').style.display = "block";
			document.getElementById('file_upload_imposbl').style.display = "none";			
		} else {
			document.getElementById('file_upload_posbl').style.display = "none";
			document.getElementById('file_upload_imposbl').style.display = "block";
		}
	}
</script>

	<c:if test="${updateFlag=='Y'}">
		<input type="hidden" name="atchFileId" value="${atchFileId}"/>
		<input type="hidden" name="fileSn" />
		<input type="hidden" name="fileListCnt" value="${fileListCnt}"/>
	</c:if>
	
   <jsp:scriptlet>
	 	pageContext.setAttribute("crlf", "\r\n");
   </jsp:scriptlet>
	<table>
      	<c:forEach var="fileVO" items="${fileList}" varStatus="status">
	      <tr>
	       <th>
	       		<c:if test="${updateFlag!='Y'}">
			       <c:if test="${'wmv' == fileVO.fileExtsn || 'avi' == fileVO.fileExtsn || 'asf' == fileVO.fileExtsn || 'wmp' == fileVO.fileExtsn || 'wm' == fileVO.fileExtsn || 'wmx' == fileVO.fileExtsn || 'mpeg' == fileVO.fileExtsn || 'mpg' == fileVO.fileExtsn || 'mp4' == fileVO.fileExtsn  || '3gp' == fileVO.fileExtsn || 'k3g' == fileVO.fileExtsn || 'mov' == fileVO.fileExtsn
	 		       		      || 'WMV' == fileVO.fileExtsn || 'AVI' == fileVO.fileExtsn || 'ASF' == fileVO.fileExtsn || 'WMP' == fileVO.fileExtsn || 'WM' == fileVO.fileExtsn || 'WMX' == fileVO.fileExtsn || 'MPEG' == fileVO.fileExtsn || 'MPG' == fileVO.fileExtsn || 'MP4' == fileVO.fileExtsn}">
				     	 <object ID="WMP" classid="CLSID:6BF52A52-394A-11D3-B153-00C04F79FAA6" standby="Loading Microsoft Windows Media Player components..." TYPE="application/x-oleobject" width="320" height="270" VIEWASTEXT title="더블클릭 하시면 크게 보실 수 있습니다.">
				         <param NAME="url" value="/common/file/file_view.do?filepath=<c:out value="${fileVO.fileStreCours}"/>&filename=<c:out value="${fileVO.streFileNm}"/>">
				         <param NAME="AutoStart" value="false">
				         <param NAME="uiMode" value="full">
				         </object><br/>
				         <a href="javascript:fn_egov_downFile('<c:out value="${fileVO.atchFileId}"/>','<c:out value="${fileVO.fileSn}"/>')">
							<c:out value="${fileVO.orignlFileNm}"/><!-- &nbsp;[<c:out value="${fileVO.fileMg}"/>&nbsp;byte]  -->
						 </a>
			       </c:if>
			       <c:if test="${'jpg' == fileVO.fileExtsn || 'JPG' == fileVO.fileExtsn || 'gif' == fileVO.fileExtsn || 'GIF' == fileVO.fileExtsn || 'png' == fileVO.fileExtsn || 'PNG' == fileVO.fileExtsn}">
			       		<a href="javascript:fn_egov_downFile('<c:out value="${fileVO.atchFileId}"/>','<c:out value="${fileVO.fileSn}"/>')">
			       			<img src="/common/file/file_view.do?filepath=<c:out value="${fileVO.fileStreCours}"/>&filename=<c:out value="${fileVO.streFileNm}"/>" style="min-width:200px;max-width:300px;"  TITLE="<c:out value="${fn:replace(fileVO.fileCn , crlf , '<br/>')}"/>" />
			       		</a>
			       		<br/>
			       		<br/>
			       </c:if>
	       		</c:if>
		       <c:if test="${'mp3' == fileVO.fileExtsn}">
				    <script>
						if (navigator.userAgent.match(/iPhone|Mobile|UP.Browser|Android|BlackBerry|Windows CE|Nokia|webOS|Opera Mini|SonyEricsson|opera mobi|Windows Phone|IEMobile|POLARIS/) != null) {
						    document.write("<audio src='/common/file/file_view.do?filepath=<c:out value="${fileVO.fileStreCours}"/>&filename=<c:out value="${fileVO.streFileNm}"/>'></audio>");
						} else {
							document.write("<audio controls='controls' autoplay style='width:90%;height:50px;' source src='/common/file/file_view.do?filepath=<c:out value="${fileVO.fileStreCours}"/>&filename=<c:out value="${fileVO.streFileNm}"/>' type='audio/mp3'></audio>");
						}
				    </script>
		       </c:if>
		       
		       <c:if test="${'mp3' == fileVO.fileExtsn}">
				       <c:if test="${updateFlag=='Y'}">
					       <br/><c:out value="${fileVO.orignlFileNm}"/>&nbsp;[<c:out value="${fileVO.fileMg}"/>&nbsp;byte] 
					       <img src="<c:url value='/images/bu5_close.gif'/>" 
					       		width="19" height="18" onclick="fn_egov_deleteFile('<c:out value="${fileVO.atchFileId}"/>','<c:out value="${fileVO.fileSn}"/>');">
				       </c:if>
		       </c:if>
		       
		       <c:if test="${'mp3' != fileVO.fileExtsn}">
			       <c:choose>
				       <c:when test="${updateFlag=='Y'}">
					       <c:out value="${fileVO.orignlFileNm}"/>&nbsp;[<c:out value="${fileVO.fileMg}"/>&nbsp;byte] 
					       <img src="<c:url value='/images/bu5_close.gif'/>" width="19" height="18" onclick="fn_egov_deleteFile('<c:out value="${fileVO.atchFileId}"/>','<c:out value="${fileVO.fileSn}"/>');">
				       </c:when>
				       <c:otherwise>
					       	<c:if test="${'wmv' != fileVO.fileExtsn && 'avi' != fileVO.fileExtsn && 'asf' != fileVO.fileExtsn && 'wmp' != fileVO.fileExtsn && 'wm' != fileVO.fileExtsn && 'wmx' != fileVO.fileExtsn && 'mpeg' != fileVO.fileExtsn && 'mpg' != fileVO.fileExtsn && 'mp4' != fileVO.fileExtsn && '3gp' != fileVO.fileExtsn && 'k3g' != fileVO.fileExtsn && 'mov' != fileVO.fileExtsn
			 		       		      && 'WMV' != fileVO.fileExtsn && 'AVI' != fileVO.fileExtsn && 'ASF' != fileVO.fileExtsn && 'WMP' != fileVO.fileExtsn && 'WM' != fileVO.fileExtsn && 'WMX' != fileVO.fileExtsn && 'MPEG' != fileVO.fileExtsn && 'MPG' != fileVO.fileExtsn && 'MP4' != fileVO.fileExtsn}">
						       <a href="javascript:fn_egov_downFile('<c:out value="${fileVO.atchFileId}"/>','<c:out value="${fileVO.fileSn}"/>')">
						       		<c:out value="${fileVO.orignlFileNm}"/><!-- &nbsp;[<c:out value="${fileVO.fileMg}"/>&nbsp;byte]  -->
						       </a>
			 		       	</c:if>
				       </c:otherwise>
			       </c:choose>
		       </c:if>
		       
	       </th>
	      </tr>  
        </c:forEach>
      </table>
