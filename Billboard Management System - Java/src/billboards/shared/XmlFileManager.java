package billboards.shared;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class XmlFileManager {
    protected Document doc;
    protected DocumentBuilder builder;

    /**
     * A default constructor for the Interpreter and Creator since both need to do this and use these variables.
     * @throws ParserConfigurationException This exception occurs if there is an issue running the program
     */
    public XmlFileManager() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }
}
