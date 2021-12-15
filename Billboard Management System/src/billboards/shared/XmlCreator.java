package billboards.shared;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XmlCreator extends XmlFileManager {
    /**
     * an enum representing the pictureType for the 'createPictureElement' method
     */
    public enum pictureType {
        url,
        data
    }

    /**
     * Creates an instance of the XMLInterpreter
     */
    public XmlCreator() throws ParserConfigurationException {
        super();
        doc = builder.newDocument();
    }

    /**
     * Creates an XML element for showing a picture.
     * @param pictureContent the picture url/data
     * @param pt the picture type (url or data)
     * @return an instance of the picture element
     */
    public Element createPictureElement(String pictureContent, pictureType pt) {
        Element picture = doc.createElement("picture");

        if(pt == pictureType.url) picture.setAttribute("url", pictureContent);
        else if(pt == pictureType.data) picture.setAttribute("data", pictureContent);

        return picture;
    }

    /**
     * Creates an XML element for showing a message.
     * @param messageContent the contents of the message
     * @param messageColour the colour of the message
     * @return an instance of the message element
     */
    public Element createMessageElement(String messageContent, String messageColour) {
        Element message = doc.createElement("message");

        message.setTextContent(messageContent);
        message.setAttribute("colour", messageColour);

        return message;
    }

    /**
     * Creates an XML element for showing a message.
     * @param messageContent the contents of the message
     * @return an instance of the message element
     */
    public Element createMessageElement(String messageContent) {
        Element message = doc.createElement("message");

        message.setTextContent(messageContent);

        return message;
    }

    /**
     * Creates an XML element for showing textual information.
     * @param informationContent the text of the information
     * @param informationColour the colour of the element
     * @return an instance of the information element
     */
    public Element createInformationElement(String informationContent, String informationColour) {
        Element information = doc.createElement("information");

        information.setTextContent(informationContent);
        information.setAttribute("colour", informationColour);

        return information;
    }

    /**
     * Creates an XML element for showing textual information.
     * @param informationContent the text of the information
     * @return an instance of the information element
     */
    public Element createInformationElement(String informationContent) {
        Element information = doc.createElement("information");

        information.setTextContent(informationContent);

        return information;
    }

    /**
     * Creates an XML file.
     * @param fileName the name of the file
     * @param billboardBackground the background for the root element
     * @param elements the elements of the file
     * @return whether or not the file creation was successful
     */
    public boolean createXmlFile(String fileName, String billboardBackground, Element[] elements) {
        Element root = doc.createElement("billboard");
        root.setAttribute("background", billboardBackground);

        for(Element element : elements) {
            if(element != null) {
                root.appendChild(element);
            }
        }

        doc.appendChild(root);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( new DOMSource(doc), new StreamResult(new File(fileName + ".xml")));
        }
        catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String createXmlString(Element[] elements) {
        Element root = doc.createElement("billboard");
        String result = "";

        for(Element element : elements) {
            root.appendChild(element);
        }

        doc.appendChild(root);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( new DOMSource(doc), new StreamResult(result));
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Creates an XML file.
     * @param fileName the name of the file
     * @param elements the elements of the file
     * @return whether or not the file creation was successful
     */
    public boolean createXmlFile(String fileName, Element[] elements) {
        Element root = doc.createElement("billboard");

        for(Element element : elements) {
            root.appendChild(element);
        }

        doc.appendChild(root);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform( new DOMSource(doc), new StreamResult(new File(fileName + ".xml")));
        }
        catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
