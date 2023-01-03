<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="name.lowbrain.SingletonSample"%>
<% 
    SingletonSample ss = SingletonSample.getInstance(); 
    request.setAttribute("key1", ss.getKey1());
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script>
    window.addEventListener('DOMContentLoaded', () => {
        console.log(document.getElementById('properties-key1').value);
    });
</script>
</head>
<body>
    <dl>
        <dt>プロパティを出力：</dt>
        <dd><textarea id="properties-key1" cols="50" rows="2" readonly><c:out value='${key1}'/></textarea></dd>
    </dl>
</body>
</html>