package name.lowbrain;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlSignatureVerifySample {

    private final Document doc;

    private final PublicKey publicKey;

    public XmlSignatureVerifySample(String _xmlString, String _publicKeyPath) {
        // XML 文章の保持
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
			this.doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(_xmlString.getBytes("Shift-JIS")));
            this.doc.setXmlStandalone(true);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

        // 公開鍵の保持
        this.publicKey = getPublicKey(_publicKeyPath);
    }

    public boolean verify() throws MarshalException, XMLSignatureException {
        boolean result = false;

        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new RuntimeException("Cannot find Signature element");
        }

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        DOMValidateContext valContext = new DOMValidateContext(publicKey, nl.item(0));
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        result = signature.validate(valContext);

        return result;
    }

    private PublicKey getPublicKey(String publicKeyPath) {
        PublicKey key = null;
        Path path = FileSystems.getDefault().getPath(publicKeyPath);
        byte[] keyByte = null;

        // 公開鍵のPEMファイル読み込み
        try (BufferedReader keyReader = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
            StringBuilder keyString = new StringBuilder();
            Stream<String> lines = keyReader.lines();
            lines.forEach(line -> {
                if (line.indexOf("-----BEGIN") < 0 && line.indexOf("-----END") < 0) {
                    keyString.append(line);
                }
            });
            keyByte = Base64.getDecoder().decode(keyString.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 公開鍵生成
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            key = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

    public static void main(String[] args) throws Exception {
        WriteXMLSample xml = new WriteXMLSample("ユーザID");
        System.out.println(xml.toXmlString());

        XmlSignatureSignSample sign = new XmlSignatureSignSample(xml.toXmlString(), SignatureSignSample.class.getResource("privateKey.pem").getFile());
        String signXml = sign.toXmlString();
        System.out.println(signXml);

        XmlSignatureVerifySample verify = new XmlSignatureVerifySample(signXml, SignatureSignSample.class.getResource("publicKey.pem").getFile());
        System.out.println(verify.verify());
    }

}
