<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="name.lowbrain.*"%>
<%@page import="name.lowbrain.dsig.*"%>
<% 
    try {
        String popup_url = request.getParameter("popup_url");
        request.setAttribute("popup_url", popup_url);

        String auth_url = request.getParameter("auth_url");
        request.setAttribute("auth_url", auth_url);

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
<title>LOGIN</title>
<style type="text/css">
.btnripple{
    /*波紋の基点とするためrelativeを指定*/
    position: relative;
    /*はみ出す波紋を隠す*/
    overflow: hidden;
    /*ボタンの形状*/
    width: 320px;
    height: 320px;
    text-align:center;
    font-size: 64px;
    line-height: 320px;
    text-decoration: none;
    display:inline-block;
    background: #6750A4;
    color: #fff;
    border-radius: 50%;
    outline: none;
}

.btnripple::after {
    content: "";
    /*絶対配置で波紋位置を決める*/
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    /*波紋の形状*/
    background: radial-gradient(circle, #fff 10%, transparent 10%) no-repeat 50%;
    transform: scale(10, 10);
    /*はじめは透過0に*/
    opacity: 0;
    /*アニメーションの設定*/
    transition: transform 0.3s, opacity 1s;
}

/*クリックされたあとの形状の設定*/
    .btnripple:active::after {
    transform: scale(0, 0);
    transition: 0s;
    opacity: 0.3;
}


/*========= レイアウトのためのCSS ===============*/

body{
    vertical-align:middle; 
    padding: 100px 0;
    text-align: center;
}

p{
    margin: 0 0 10px 0;
}

</style>
</head>
<body onunload="subclose();">
    <form name="formTest" action="" method="post">
        <input type="hidden" name="popup_url"    value="${popup_url}"    />
        <input type="hidden" name="auth_url"     value="${auth_url}"     />
        <input type="hidden" name="return_url"   value="${return_url}"   />
        <input type="hidden" name="redirect_url" value="${redirect_url}" />
        <input type="hidden" name="auth_token"   value="${auth_token}"   />
    </form>
    <a href="javascript:init();" class="btnripple">LOGIN!!</a>
<script>
var subwin = null;
function init() {
    subwin = window.open(formTest.popup_url.value, "_blank", "popup");
    setTimeout("next()", 1000);
}

function next() {
    if(subwin) subwin.close();
    formTest.action = formTest.auth_url.value;
    formTest.submit();
    return;
}

function subclose() {
    if(subwin) subwin.close();
}
</script>
</body>
</html>