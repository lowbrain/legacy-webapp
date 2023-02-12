<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
    try {
        String url = request.getParameter("jump_url");
        request.setAttribute("jump_url", url);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body onload="init()">
    <form name="formTest" action="" method="get">
        <input type="hidden" name="jump_url" value="${jump_url}" />
    </form>
<script>
function init() {
    formTest.action = formTest.jump_url.value;
    formTest.submit();
    return;
}
</script>
</body>
</html>