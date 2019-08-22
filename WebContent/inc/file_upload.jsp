<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

				<script language="javascript">
				   	append_cnt = 0;
				   	attach_cnt = ++fileAttachIndex;
				   	
				   	var i_maxCnt = 3;
				   	if(window.fileUploadMaxCnt)
				   		i_maxCnt = fileUploadMaxCnt;
				   	else
					   	i_maxCnt = 3;
	
				   	function insert_attach () {
				   		if(attach_cnt == i_maxCnt) {
				   			alert(i_maxCnt+'개 이상은 등록 하실 수 없습니다.');
				   			return;
				   		}
					   	if (append_cnt == 0)
						   	eval ('i_enter').innerHTML += "<br/>";
					   	append_cnt++;
					   	var insertTag = "";
					   	insertTag += "		<table border='0' cellpadding='0' cellspacing='0'>";
					   	insertTag += "		<tr>";
					   	insertTag += "			<td>&nbsp;";
					   	insertTag += "				<input type=file name=file_" + attach_cnt + " size=30> <textarea name='fileCn' cols=25 rows=2></textarea>";
					   	insertTag += "			</td>";
					   	insertTag += "		</tr>";
					   	insertTag += "		</table>";
					   	insertTag += "		<div id='attach_div" + (attach_cnt+1) + "'></div>";
					   		
					   	eval ('attach_div' + attach_cnt).innerHTML = insertTag + eval ('attach_div' + attach_cnt).innerHTML;
					   	attach_cnt++;
				   	}
				</script>
	
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td style="padding:0 0 0 4px;">
										<script type="text/javascript" language="javascript">
											document.write("&nbsp;<input type='file' name='file_"+(attach_cnt-1)+"' size='30'>");
										</script>
									</td>
								</tr>
							</table>
							<script type="text/javascript" language="javascript">
							if(fileUploadMaxCnt>1) {
								document.write("<div id=attach_div"+(attach_cnt)+"></div>");
							}
							</script>
						</td>
						<td valign="top" style="vertical-align: bottom;">
							<script type="text/javascript" language="javascript">
							if(fileUploadMaxCnt>1) {
								document.write("<div class='cmm_btn_20' style='padding-left:3px;'><a href='javascript:insert_attach ()'>행추가</a></div><div id='i_enter'></div>");
							}
							</script>
						</td>
					</tr>
				</table>