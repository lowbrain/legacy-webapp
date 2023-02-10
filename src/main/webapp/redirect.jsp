<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
    try {
        String url = request.getParameter("return_url");
        String auth = request.getParameter("auth_token");
        String redirectUlr = url + "?auth=" + auth;
        response.sendRedirect(redirectUlr);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>