<%@ page import="io.nodehome.svm.common.util.StringUtil" %>
<%@ page import="io.nodehome.cmm.ui.pagination.PaginationInfo"%>

<%
	PaginationInfo paginationInfo = (PaginationInfo)request.getAttribute("paginationInfo");

	int p_cols = paginationInfo.getPageSize();
	int p_rows = paginationInfo.getRecordCountPerPage();
	int p_total = paginationInfo.getTotalRecordCount();
	int p_page = paginationInfo.getCurrentPageNo();

	int pg_count = 0;
	int block_count = 0;
	int first = 0;
	int last = 0;
	int block = 0;
	int pg_first = 0;
	int pg_last = 0;
	String v_ex_link = "";
	
	if (p_total%p_rows==0) pg_count=p_total/p_rows;
	else pg_count=(p_total/p_rows)+1;

	if (pg_count%p_cols==0)block_count=pg_count/p_cols;
	else block_count=pg_count/p_cols+1;

	first=p_rows*(p_page-1);
	last=p_rows*p_page;
	if (last>p_total) last=p_total;

	if (p_page%p_cols==0) block=p_page/p_cols;
	else block=(p_page/p_cols)+1;
	pg_first=p_cols*(block-1)+1;
	pg_last=p_cols*block;
	if (pg_last>pg_count) pg_last=pg_count;
	
	if (p_total > 0) {
		out.println ("<table border='0' cellspacing='0' cellpadding='0' width='100%'><tr><td align='center'>");
		out.println ("<table border='0' cellspacing='0' cellpadding='0'>");
		out.println ("<tr height=30>");
		
		if (p_str != null) {
			for (int i = 0; i < p_str.length; i+=2) {
				if (p_str[i+1] != null)
					v_ex_link += "&" + p_str[i] + "=" + p_str[i+1];
			}
		}
		
		
		
		if(block > 1) {
		    int back_page_= (block-1)*p_cols;

			if(p_link_script != null && !p_link_script.equals("")) {
				out.println ("<td width='30' align='center'><a href='#none' onclick='javascript:"+p_link_script+"("+back_page_+");'><<</a></td>");
			} else {
				out.println ("<td width='30' align='center'><a href='"+p_link_url+"?"+p_pageObjectId+"="+((block-1)*p_cols)+v_ex_link+"'><<</a></td>");
			}
		} else {
			out.println ("<td width='30' align='center'><a href='#none'><<</a></td>");
		}
		
		
		
		
		if(p_page > 1) {
		 	if(p_link_script != null && !p_link_script.equals("")) 
		 		out.println ("<td style='padding-left:5px;' align='center'><a href='javascript:"+p_link_script+"("+(p_page-1)+");'><</a></td>");
		 	else
		 		out.println ("<td style='padding-left:5px;' align='center'><a href='"+p_link_url+"?page="+(p_page-1)+v_ex_link+"'><</a></td>");
		} else {
			out.println ("<td style='padding-left:5px;' align='center'><a href='#none'><</a></td>");
		}

		
		
		
		for (int i = pg_first; i <= pg_last; i++) {
			if (i == p_page) {
				out.println ("<td width='30' style='padding-left:5px;' align='center'>");
				out.println ("<div class='cmm_pagebar_30' style='background:#ffffff;'><a href='#none'>"+p_page+"</a></span>");
				out.println ("</td>");
			} else {
				out.println ("<td width='30' style='padding-left:5px;' align='center'>");
				out.println ("<div class='cmm_pagebar_30'>");
					
					out.println ("<a href='"+p_link_url+"?"+p_pageObjectId+"="+i+v_ex_link+"'>"+i+"</a>");
				
				/*	
				if(p_link_script != null && !p_link_script.equals("")) {
					out.println ("<a href='#none' onclick='javascript:"+p_link_script+"("+i+");'>"+i+"</a>");
				} else {
					out.println ("<a href='"+p_link_url+"?"+p_pageObjectId+"="+i+v_ex_link+"'>"+i+"</a>");
				}
				*/
				
				out.print("</div>");
				out.println ("</td>");
			}
		}
		
		

		if(p_page < pg_count) {
		 	if(p_link_script != null && !p_link_script.equals("")) {
		 		int t_p_page = p_page+1;
		 		out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='javascript:"+p_link_script+"("+(t_p_page)+");'>></a></td>");
		 	} else {
		 		out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='"+p_link_url+"?page="+(p_page+1)+v_ex_link+"'>></a></td>");
		 	}
		} else {
			out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='#none'>></a></td>");
		}
		
		
		
		if (block < block_count) {
		    int next_page_= (block*p_cols)+1;

			if(p_link_script != null && !p_link_script.equals("")) {
				out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='#none' onclick='"+p_link_script+"("+next_page_+");'>>></a></td>");
			} else {
				out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='"+p_link_url+"?"+p_pageObjectId+"="+(block*p_cols+1)+v_ex_link+"'>>></a></td>");
			}
		} else {
			out.println ("<td width='30' style='padding-left:5px;' align='center'><a href='#none'>>></a></td>");
		}

		out.println ("</tr>");
		out.println ("</table>");
		out.println ("</td></tr></table>");
	}

%>
		

