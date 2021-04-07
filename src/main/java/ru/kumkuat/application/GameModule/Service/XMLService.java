package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.kumkuat.application.GameModule.Exceptions.RepliesException;
import ru.kumkuat.application.GameModule.Exceptions.TriggerException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j

@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class XMLService {
    private DocumentBuilderFactory builderFactory;
    private XPathFactory xpathFactory = null;
    private DocumentBuilder builder = null;
    private XPath xpath = null;
    private Document doc = null;
    @Value("${xml.pathToScenario2}")
    private String path;

    public XMLService() {
        builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        try {
            xpathFactory = XPathFactory.newInstance();
            xpath = xpathFactory.newXPath();
            builder = builderFactory.newDocumentBuilder();
            doc = builder.parse("../resources/scenario_template.xml" /*"classes/scenario_template.xml"*/);
        } catch (SAXException e) {
            e.printStackTrace();
            log.debug("XMLService:SAX exception with XMLService.");
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("XMLService: Path wrong or empty.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            log.debug("XMLService: Parser config wrong.");
        }


    }

    public Node getTriggerNode(Node scene) throws TriggerException {
        var nodes = scene.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals("trigger")) {
                return nodes.item(i).getChildNodes().item(1);
            }
        }
        throw new TriggerException("EXCEPTION: Trigger is not found");
    }

    public ArrayList<Node> getRepliesNodes(Node scene) throws RepliesException {
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
                if (replies.size() == 0) {
                    throw new RepliesException("EXCEPTION: Replies are empty");
                }
                return replies;
            }
        }
        throw new RepliesException("EXCEPTION: Replies are not found");
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
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            log.debug("XPath bot found.");
        }
        return replies.size() > 0 ? replies : null;
    }
}
