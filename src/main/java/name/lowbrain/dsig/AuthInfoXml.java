package name.lowbrain.dsig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class AuthInfoXml {
    
    private final String userId;

    private final LocalDateTime timestamp;

    private final Document doc;

    public AuthInfoXml(String _userId) {
        this(_userId, LocalDateTime.now(ZoneId.of("Asia/Tokyo")), null);
    }

    public AuthInfoXml(String _userId, LocalDateTime _timestamp) {
        this(_userId, _timestamp, null);
    }

    private AuthInfoXml(String _userId, LocalDateTime _timestamp, Document _doc) {
        this.userId = _userId;
        this.timestamp = _timestamp;
        this.doc = _doc != null ? _doc : generateDocument();
    }

    public String getUserId() {
        return this.userId;
    }

    public LocalDateTime getTimeStamp() {
        return this.timestamp;
    }

    public Document getDocument() {
        return this.doc.cloneNode(true).getOwnerDocument();
    }

    public byte[] getByte() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        try {
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.transform(new DOMSource(doc), new StreamResult(stream));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        return stream.toByteArray();
    }

    public String toXmlString() {
        return new String(getByte());
    }

    public static AuthInfoXml parse(byte[] data) {
        AuthInfoXml instance = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            Document readDoc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(data));
            readDoc.setXmlStandalone(true);
            
            String userId = readDoc.getElementsByTagName("userid").item(0).getTextContent();
            String timestamp = readDoc.getElementsByTagName("timestamp").item(0).getTextContent();

            instance = new AuthInfoXml(userId, LocalDateTime.parse(timestamp), readDoc); 
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    private Document generateDocument() {
        Document doc = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            doc = factory.newDocumentBuilder().newDocument();
            doc.setXmlStandalone(true);
                        
            Element info = doc.createElement("authtoken");
            doc.appendChild(info);

            Element name = doc.createElement("userid");
            name.appendChild(doc.createTextNode(this.userId));
            info.appendChild(name);

            Element timestamp = doc.createElement("timestamp");
            timestamp.appendChild(doc.createTextNode(this.timestamp.toString()));
            info.appendChild(timestamp);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return doc;
    }

}
