package name.lowbrain;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.stream.Stream;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlSignatureSignSample {

    private final Document doc;

    private final PrivateKey privateKey;

    public XmlSignatureSignSample(String _xmlString, String _privateKeyPath) {
        // XML 文章の保持
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
			this.doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(_xmlString.getBytes("Shift-JIS")));
            this.doc.setXmlStandalone(true);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

        // 秘密鍵の保持
        this.privateKey = getPrivateKey(_privateKeyPath);
    }

    public String toXmlString() throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference
            ("", fac.newDigestMethod(DigestMethod.SHA256, null),
             Collections.singletonList
              (fac.newTransform
                (Transform.ENVELOPED, (TransformParameterSpec) null)),
             null, null);

        SignedInfo si = fac.newSignedInfo
            (fac.newCanonicalizationMethod
             (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
              (C14NMethodParameterSpec) null),
             fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
             Collections.singletonList(ref));

        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
        XMLSignature signature = fac.newXMLSignature(si, null);
        signature.sign(dsc);

        StringWriter xmlWriter = new StringWriter();
        
        TransformerFactory tfFactory = TransformerFactory.newInstance();
        Transformer tf = tfFactory.newTransformer();
        tf.transform(new DOMSource(doc), new StreamResult(xmlWriter));
 
        return xmlWriter.toString();
    }

    public byte[] toXmlByteArray() {
        try {
            return toXmlString().getBytes("Shift_JIS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PrivateKey getPrivateKey(String privateKeyPath) {
        PrivateKey key = null;
        Path path = FileSystems.getDefault().getPath(privateKeyPath);
        byte[] keyByte = null;

        // 秘密鍵のPEMファイル読み込み
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

        // 秘密鍵生成
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            key = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return key;
    }

    public static void main(String[] args) throws Exception {
        WriteXMLSample xml = new WriteXMLSample("ユーザID");
        System.out.println(xml.toXmlString());

        XmlSignatureSignSample sign = new XmlSignatureSignSample(xml.toXmlString(), SignatureSignSample.class.getResource("privateKey.pem").getFile());
        System.out.println(sign.toXmlString());
    }
}
