<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%
	String hostList = (String)request.getAttribute("hostList");
	out.clear();
	out.print(hostList);
%>
