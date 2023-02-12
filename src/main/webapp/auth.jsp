<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="name.lowbrain.*"%>
<%@page import="name.lowbrain.dsig.*"%>
<% 
    try {
        String return_url = request.getParameter("return_url");
        request.setAttribute("return_url", return_url);

        String redirect_url = request.getParameter("redirect_url");
        request.setAttribute("redirect_url", redirect_url);

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
<body onload="init()">
    <form name="formTest" action="" method="post">
        <input type="hidden" name="return_url"   value="${return_url}"   />
        <input type="hidden" name="redirect_url" value="${redirect_url}" />
        <input type="hidden" name="auth_token"   value="${auth_token}"   />
    </form>
<script>
function init() {
    formTest.action = formTest.return_url.value;
    formTest.submit();
    return;
}
</script>
</body>
</html>