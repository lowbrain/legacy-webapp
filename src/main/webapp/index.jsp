<%@page import="name.lowbrain.SingletonSample"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% SingletonSample ss = SingletonSample.getInstance(); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%= ss.getKey1() %>
</body>
</html>