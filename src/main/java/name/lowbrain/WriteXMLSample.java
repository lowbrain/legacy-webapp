package name.lowbrain;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXMLSample {

    private final String userId;

    private final LocalDateTime timestamp;

    private final String xmlString;

    public WriteXMLSample(String _userId) {
        this.userId = _userId;
        this.timestamp = LocalDateTime.now();
        this.xmlString = generateXmlString();
    }

    public String getUserId() {
        return this.userId;
    }

    public LocalDateTime getTimeStamp() {
        return this.timestamp;
    }

    public String toXmlString() {
        return this.xmlString;
    }

    public byte[] toXmlByteArray() {
        try {
            return this.xmlString.getBytes("Shift_JIS");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String generateXmlString() {
        StringWriter xmlWriter = new StringWriter();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.setXmlStandalone(true);
                        
            Element info = doc.createElement("authtoken");
            doc.appendChild(info);

            Element name = doc.createElement("userid");
            name.appendChild(doc.createTextNode(this.userId));
            info.appendChild(name);

            Element timestamp = doc.createElement("timestamp");
            timestamp.appendChild(doc.createTextNode(this.timestamp.toString()));
            info.appendChild(timestamp);
            
            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.setOutputProperty("encoding", "Shift-JIS");
            tf.transform(new DOMSource(doc), new StreamResult(xmlWriter));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }

        return xmlWriter.toString();
    }
}
