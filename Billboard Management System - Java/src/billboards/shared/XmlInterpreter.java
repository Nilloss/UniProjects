package billboards.shared;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class XmlInterpreter extends XmlFileManager{

    /**
     *
     * @param data Raw XML or a filename
     * @param isFile whether or not 'data' is a filename
     * @throws ParserConfigurationException is thrown by XmlFileManager
     * @throws IOException is thrown in by the builder when parsing to the document variable if something goes wrong
     * @throws SAXException is thrown in by the builder when parsing to the document variable if something goes wrong
     */
    public XmlInterpreter(String data, boolean isFile ) throws ParserConfigurationException, IOException, SAXException  {
        super();
        if(isFile) doc = builder.parse(data);
        else {
            InputSource is = new InputSource(new StringReader(data));
            doc = builder.parse(is);
        }
    }

    /**
     * Converts the xml to a String matrix.
     * @return a String matrix where each element is an array corresponding to the
     * elements various attributes and content
     */
    public String[][] xmlToStringMatrix() {
        Element billboardTag = (Element)doc.getElementsByTagName("billboard").item(0);
        NodeList childNodes = billboardTag.getChildNodes();

        for(int i = 0; i < childNodes.getLength(); ++i) {
            Node n = childNodes.item(i);
            if(n.getNodeName().equals("#text")) {
                childNodes.item(i).getParentNode().removeChild(n);
            }
        }

        String[][] Elements = new String[childNodes.getLength() + 1][4];
        Elements[0][0] = billboardTag.getAttribute("background");
        for(int i = 0; i < childNodes.getLength(); ++i) {
            Elements[i + 1][0] = childNodes.item(i).getNodeName();
            Elements[i + 1][1] = childNodes.item(i).getTextContent();

            NamedNodeMap nnm = childNodes.item(i).getAttributes();
            if(nnm.getLength() != 0) {
                Elements[i + 1][2] = nnm.item(0).getNodeName();
                Elements[i + 1][3] = nnm.item(0).getNodeValue();
            }
        }

        return Elements;
    }

    /**
     * A main function with an example of usage for the class
     * @param args command line arguments - which are not in use
     */
    public static void main(String[] args) {
        try {
            XmlInterpreter interpreter = new XmlInterpreter("<?xml version=\"1.0\" encoding=\"UTF-8\" " +
                    "standalone=\"no\"?><billboard background=\"#00FFFF\"><information colour=\"#00FFFF\">info" +
                    "</information><message colour=\"#00FFFF\">hey</message><picture url=\"www\"/></billboard>",
                    false
            );
            String[][] result = interpreter.xmlToStringMatrix();
            for(String[] res : result){
                System.out.println("[");
                for(String r : res) {
                    System.out.println("    " + r);
                }
                System.out.println("]");
            }
        }
        catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}
