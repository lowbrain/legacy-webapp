<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="name.lowbrain.*"%>
<%@page import="name.lowbrain.dsig.*"%>
<% 
    try {
        String url = request.getParameter("url");
        request.setAttribute("return_url", url);

        AuthInfoXml xml = new AuthInfoXml("ユーザID");
        AuthInfoToken outToken = AuthInfoToken.newInstance(xml);
        String out1 = outToken.toBase64Token();
        request.setAttribute("auth_token", out1);
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
<body onload="init()" onunload="subclose()">
    <form name="formTest" action="" method="post">
        <input type="hidden" name="return_url" value="${return_url}" />
        <input type="hidden" name="auth_token" value="${auth_token}" />
    </form>
<script>
var subwin = null;
function init() {
    subwin = window.open("http://www.yahoo.co.jp", "_blank", "popup");
    setTimeout("next()", 5000);
}

function next() {
    if(subwin) subwin.close();
    formTest.action = "redirect.jsp";
    formTest.submit();
    return;
}

function subclose() {
    if(subwin) subwin.close();
}
</script>
</body>
</html>