package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.kumkuat.application.GameModule.Collections.Scene;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
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
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public Node getTriggerNode(Node scene) throws Exception {
        var nodes = scene.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals("trigger")) {
                return nodes.item(i).getChildNodes().item(1);
            }
        }
        throw new Exception("EXCEPTION: Trigger is not found");
    }

    public ArrayList<Node> getRepliesNodes(Node scene) throws Exception {
        var nodes = scene.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals("replies")) {
                ArrayList<Node> replies = new ArrayList<>();
                NodeList replyNodes = nodes.item(i).getChildNodes();
                for (int j = 0; j < replyNodes.getLength(); j++) {
                    if (replyNodes.item(j).getNodeName().equals("reply")) {
                        replies.add(replyNodes.item(j));
                    }
                }
                if (replies.stream().count() == 0) {
                    throw new Exception("EXCEPTION: Replies are empty");
                }
                return replies;
            }
        }
        throw new Exception("EXCEPTION: Replies are not found");
    }

    public ArrayList<Node> getSceneNodes() {
        NodeList nodes = null;
        ArrayList<Node> replies = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xpath.compile(
                    "//scene"
            );
            nodes = (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeName().equals("scene")) {
                    replies.add(nodes.item(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return replies.stream().count() > 0 ? replies : null;
    }
}
