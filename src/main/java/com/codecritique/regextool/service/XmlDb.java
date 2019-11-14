package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class XmlDb {
    private final String location;
    private Document document;
    private XPath xpath;

    XmlDb(String location) {
        this.location = location;
        xpath = XPathFactory.newInstance().newXPath();
    }

    void init() {
        this.read();
    }

    Regex get(int id) {
        try {
            read();
            Node node = (Node) xpath.evaluate("storage/regex[@id=" + id + "]", document, XPathConstants.NODE);
            return toRegexObject(node);
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    List<Regex> getAll() {
        try {
            read();
            NodeList entities = (NodeList) xpath.evaluate("storage/regex", document, XPathConstants.NODESET);
            List<Regex> list = new ArrayList<>();
            for (int i = 0; i < entities.getLength(); i++) {
                list.add(toRegexObject(entities.item(i)));
            }
            return list;
        } catch (Exception e) {
            throw new StorageException("Couldn't get entities: " + e.getMessage());
        }
    }

    void store(Regex regex) {
        try {
            int id;
            Element root = (Element) xpath.evaluate("/storage", document, XPathConstants.NODE);
            NodeList entities = root.getElementsByTagName("regex");
            if (entities.getLength() == 0) {
                id = 1;
            } else {
                Element last = (Element) entities.item(entities.getLength() - 1);
                id = Integer.parseInt(last.getAttribute("id")) + 1;
            }
            regex.setId(id);
            root.appendChild(toRegexNode(regex));
            save();
        } catch (Exception e) {
            throw new StorageException("Couldn't store regex: " + e.getMessage());
        }
    }

    void update(Regex regex) {
        try {
            read();
            Element node = (Element) xpath.evaluate("storage/regex[@id=" + regex.getId() + "]", document, XPathConstants.NODE);
            node.getElementsByTagName("value").item(0).setTextContent(regex.getValue());
            node.getElementsByTagName("description").item(0).setTextContent(regex.getDescription());
            node.getElementsByTagName("text").item(0).setTextContent(regex.getText());
            save();
        } catch (Exception e) {
            throw new StorageException("Couldn't update regex: " + e.getMessage());
        }
    }

    void delete(int id) {
        try {
            read();
            Node root = (Node) xpath.evaluate("/storage", document, XPathConstants.NODE);
            Node regex = (Node) xpath.evaluate("storage/regex[@id=" + id + "]", document, XPathConstants.NODE);
            root.removeChild(regex);
            save();
        } catch (Exception e) {
            throw new StorageException("Couldn't delete regex: " + e.getMessage());
        }
    }

    private Regex toRegexObject(Node node) throws XPathExpressionException {
        int id = Integer.parseInt(xpath.evaluate("@id", node));
        String value = decoded(xpath.evaluate("value", node));
        String text = decoded(xpath.evaluate("text", node));
        String description = decoded(xpath.evaluate("description", node));
        return new Regex(id, value, description, text);
    }

    private Node toRegexNode(Regex regex) {
        Element parent = document.createElement("regex");
        parent.setAttribute("id", regex.getId() + "");
        NodeBuilder builder = new NodeBuilder(parent);
        return builder
                .append("value", encoded(regex.getValue()))
                .append("description", encoded(regex.getDescription()))
                .append("text", encoded(regex.getText()))
                .build();
    }

    private void read() {
        try {
            Resource resource = new FileSystemResource(location);
            InputStream in = resource.getInputStream();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(new InputSource(in));
            in.close();
        } catch (Exception e) {
            throw new StorageException("Couldn't load db: " + e.getMessage());
        }
    }

    private void save() throws Exception {
        DOMSource source = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Resource resource = new FileSystemResource(location);
        transformer.transform(source, new StreamResult(resource.getFile()));
    }

    private String encoded(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decoded(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    class NodeBuilder {
        private Node parent;

        NodeBuilder(Node parent) {
            this.parent = parent;
        }

        NodeBuilder append(String childTagName, String textValue) {
            Element child = document.createElement(childTagName);
            child.setTextContent(textValue);
            parent.appendChild(child);
            return this;
        }

        Node build() {
            return this.parent;
        }

    }

}
