<%@page language="java" contentType="text/html; charset=Shift_JIS" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="name.lowbrain.*"%>
<%@page import="name.lowbrain.dsig.*"%>
<% 
    try {
        SingletonSample ss = SingletonSample.getInstance(); 
        request.setAttribute("key1", ss.getKey1());

        AuthInfoXml xml = new AuthInfoXml("ユーザID");
        request.setAttribute("xml", xml.toXmlString());

        EncriptStringSample encript = new EncriptStringSample(EncriptStringSample.class.getResource("privateKey.pem").getFile());
        String encodeText = encript.encrypt(xml.getByte());
        request.setAttribute("encode", encodeText);

        DecriptStringSample decript = new DecriptStringSample(DecriptStringSample.class.getResource("publicKey.pem").getFile());
        byte[] decodeByte = decript.decrypt(encodeText);
        request.setAttribute("decode", new String(decodeByte, "UTF-8"));

        SignatureSignSample sign = new SignatureSignSample(SignatureSignSample.class.getResource("privateKey.pem").getFile());
        String base64Sign = sign.sign(xml.getByte());
        request.setAttribute("sign", base64Sign);

        SignatureVerifySample verify = new SignatureVerifySample(SignatureVerifySample.class.getResource("publicKey.pem").getFile());
        request.setAttribute("verify", verify.verify(xml.getByte(), base64Sign));

        AuthInfoToken outToken = AuthInfoToken.newInstance(xml);
        String out1 = outToken.toBase64Token();
        request.setAttribute("outtoken", out1);

        AuthInfoToken inToken = new AuthInfoToken(out1);
        AuthInfoXml inXml = inToken.getValidatedAuthInfoXml();
        request.setAttribute("intoken", inXml.toXmlString());

    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="Shift_JIS">
<title>Insert title here</title>
<script>
    window.addEventListener('DOMContentLoaded', () => {
        console.log(document.getElementById('properties-key1').value);
        console.log(document.getElementById('rsa-xml').value);
        console.log(document.getElementById('rsa-encode').value);
        console.log(document.getElementById('rsa-decode').value);
    });
</script>
</head>
<body>
    <dl>
        <dt>プロパティを出力：</dt>
        <dd><textarea id="properties-key1" cols="200" rows="2" readonly><c:out value='${key1}'/></textarea></dd>
    </dl>
    <dl>
        <dt>XML：</dt>
        <dd><textarea id="rsa-xml" cols="200" rows="2" readonly><c:out value='${xml}'/></textarea></dd>
    </dl>
    <dl>
        <dt>ENCODE：</dt>
        <dd><textarea id="rsa-encode" cols="200" rows="2" readonly><c:out value='${encode}'/></textarea></dd>
    </dl>
    <dl>
        <dt>DECODE：</dt>
        <dd><textarea id="rsa-decode" cols="200" rows="2" readonly><c:out value='${decode}'/></textarea></dd>
    </dl>
    <dl>
        <dt>SIGN：</dt>
        <dd><textarea id="sign" cols="200" rows="2" readonly><c:out value='${sign}'/></textarea></dd>
    </dl>
    <dl>
        <dt>VERIFY：</dt>
        <dd><textarea id="verify" cols="200" rows="2" readonly><c:out value='${verify}'/></textarea></dd>
    </dl>
    <dl>
        <dt>OUT-TOKEN</dt>
        <dd><textarea id="out-token" cols="200" rows="2" readonly><c:out value='${outtoken}'/></textarea></dd>
    </dl>
    <dl>
        <dt>IN-TOKEN：</dt>
        <dd><textarea id="in-token" cols="200" rows="2" readonly><c:out value='${intoken}'/></textarea></dd>
    </dl>

</body>
</html>