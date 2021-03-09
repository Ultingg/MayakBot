package ru.kumkuat.application.LoadModule;

import com.thoughtworks.xstream.XStream;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class XMLService {
    public XMLService(Scene scene) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        DocumentBuilder builder = null;
        try {
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse("src/main/resources/scenario_template.xml");

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
                ParserConfigurationException e) {
            e.printStackTrace();
        } catch (
                SAXException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        } catch (
                XPathExpressionException e) {
            e.printStackTrace();
        }


//
//        System.out.println(xml);
    }
}
