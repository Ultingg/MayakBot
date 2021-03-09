package ru.kumkuat.application.GameModule.Service;

import com.thoughtworks.xstream.XStream;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Models.Geolocation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class XMLService {
    private DocumentBuilderFactory builderFactory;
    private XPathFactory xpathFactory = null;
    private DocumentBuilder builder = null;
    private XPath xpath = null;
    private Document doc = null;

    public XMLService(Scene scene) {
        builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        try {
            xpathFactory = XPathFactory.newInstance();
            xpath = xpathFactory.newXPath();
            builder = builderFactory.newDocumentBuilder();
            doc = builder.parse("src/main/resources/scenario_template.xml");

            XPathExpression xPathExpression = xpath.compile(
                    "//location"
            );
            NodeList nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);

            ArrayList Locations = new ArrayList();
            XStream xstream = new XStream();
            xstream.alias("Geolocation", Geolocation.class);

            String xml = xstream.toXML(scene);

            for (int i = 0; i < nodes.getLength(); i++) {
                Geolocation geolocation = new Geolocation();

                var nodelist = nodes.item(i).getChildNodes();
                for (int j = 0; j < nodelist.getLength(); j++) {
                    var name = nodelist.item(j).getNodeName();
                    if (name.equals("fullName")) {
                        geolocation.setFullName(nodelist.item(j).getFirstChild().getNodeValue());
                    } else if (name.equals("longitude")) {
                        var longitude = Double.parseDouble(nodelist.item(j).getFirstChild().getNodeValue());
                        geolocation.setLongitude(longitude);
                    } else if (name.equals("latitude")) {
                        var latitude = Double.parseDouble(nodelist.item(j).getFirstChild().getNodeValue());
                        geolocation.setLatitude(latitude);
                    }
                }
                System.out.println("fullName");
                System.out.println(geolocation.getFullName());
                System.out.println("longitude");
                System.out.println(geolocation.getLongitude().toString());
                System.out.println("latitude");
                System.out.println(geolocation.getLatitude().toString());
            }
        } catch (
                ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    public Integer getCountScene() {
        NodeList nodes = null;
        try {
            XPathExpression xPathExpression = xpath.compile(
                    "//scene"
            );
            nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nodes.getLength();
    }

    public Node getTriggerNode(Long sceneId) {
        NodeList nodes = null;
        try {
            XPathExpression xPathExpression = xpath.compile(
                    "//scene[@number='" + sceneId + "']/trigger"
            );
            nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nodes.getLength() > 0 ? nodes.item(0).getChildNodes().item(1) : null;
    }

    public ArrayList<Node> getRepliesNodes(Long sceneId) {
        NodeList nodes = null;
        ArrayList<Node> replies = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xpath.compile(
                    "//scene[@number='" + sceneId + "']/replies/reply"
            );
            nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeName().equals("reply")) {
                    replies.add(nodes.item(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return replies.stream().count() > 0 ? replies : null;
    }
}
