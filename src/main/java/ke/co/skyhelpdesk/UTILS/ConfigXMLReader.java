package ke.co.skyhelpdesk.UTILS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;

public class ConfigXMLReader {
//    public static LinkedHashMap<String,Object> xmlReader() {
    public static LinkedHashMap<String,Object> xmlReader() {
//public static void main(String[] args) {

    LinkedHashMap <String,Object> xmlReaderMap = new LinkedHashMap<>();
        try {
            File inputFile = new File("configuration.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "/config/database";
            String expression1 = "/config/undertow";
//            String expression2 = "//password[@password_format = 'plain_text']";
            String expression2 = "//password[@password_format]";
            String expression3 = "/config/classForName";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            NodeList nodeList1 = (NodeList) xPath.compile(expression1).evaluate(doc, XPathConstants.NODESET);
            NodeList nodeList2 = (NodeList) xPath.compile(expression2).evaluate(doc, XPathConstants.NODESET);
            Node node = nodeList2.item(0);
            NodeList nodeList3 = (NodeList) xPath.compile(expression3).evaluate(doc, XPathConstants.NODESET);
            Element element = null;

            //*******************************************
            if(node.getNodeType() == Node.ELEMENT_NODE){
//                System.out.println("\nCurrent Element 2 :" + node.getNodeName());
                element = (Element) node;
//                System.out.println("element.getAttribute(password_format) :" + element.getAttribute("password_format"));

            }

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
//                System.out.println("url : " + eElement.getElementsByTagName("url").item(0).getTextContent());
//                System.out.println("username : " + eElement.getElementsByTagName("username").item(0).getTextContent());
//                System.out.println("password : " + eElement.getElementsByTagName("password").item(0).getTextContent());
                if(element != null){
                    if (element.getAttribute("password_format").equals("plain_text")) {

                        xmlReaderMap.put("url", eElement.getElementsByTagName("url").item(0).getTextContent());
                        xmlReaderMap.put("username", eElement.getElementsByTagName("username").item(0).getTextContent());
                        xmlReaderMap.put("password", eElement.getElementsByTagName("password").item(0).getTextContent());

                        element.setAttribute("password_format", "encrypted");
                        element.setTextContent(AES.encrypt(eElement.getElementsByTagName("password").item(0).getTextContent()));

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        StringWriter writer = new StringWriter();
                        transformer.transform(new DOMSource(doc), new StreamResult(writer));
//                        String xmlString = writer.getBuffer().toString();
//                        System.out.println("xmlString===" + xmlString);

                        // write the content on console
                        DOMSource source = new DOMSource(doc);
//                        System.out.println("-----------Modified File-----------");
                        StreamResult consoleResult = new StreamResult(System.out);
                        transformer.transform(source, consoleResult);

                        //update the file
                        FileOutputStream outStream = new FileOutputStream("configuration.xml");
                        transformer.transform(source, new StreamResult(outStream));
                    } else {
                        xmlReaderMap.put("url", eElement.getElementsByTagName("url").item(0).getTextContent());
                        xmlReaderMap.put("username", eElement.getElementsByTagName("username").item(0).getTextContent());
                        xmlReaderMap.put("password", AES.decrypt(eElement.getElementsByTagName("password").item(0).getTextContent()));
                    }
                }
            }
            }

            for (int i = 0; i < nodeList1.getLength(); i++) {
                Node nNode1 = nodeList1.item(i);
//                System.out.println("\nCurrent Element :" + nNode1.getNodeName());
                if (nNode1.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode1;
//                    System.out.println("eElement:" + eElement);
//                    System.out.println("port : " + eElement.getElementsByTagName("port").item(0).getTextContent());
//                    System.out.println("host : " + eElement.getElementsByTagName("host").item(0).getTextContent());
                    xmlReaderMap.put("port",eElement.getElementsByTagName("port").item(0).getTextContent());
                    xmlReaderMap.put("host",eElement.getElementsByTagName("host").item(0).getTextContent());
//                    System.out.println("xmlReaderMap:"+xmlReaderMap);
                }
            }

            for (int j = 0; j < nodeList3.getLength(); j++) {
                Node nNode2 = nodeList3.item(j);
//                System.out.println("\nCurrent Element :" + nNode2.getNodeName());

                if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode2;
//                System.out.println("eElement.getTextContent():"+eElement.getTextContent());
                xmlReaderMap.put("className",eElement.getTextContent());
                }

            }

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException |
                 TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return xmlReaderMap;
    }
}
