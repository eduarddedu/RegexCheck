package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class XmlStorageService implements RegexStorageService {
    private final String location;
    private Document document;
    private XPath xpath;
    private IdGenerator generator = new IncrementalIdGenerator();

    public XmlStorageService(StorageProperties properties) {
        this.location = properties.getLocation();
        xpath = XPathFactory.newInstance().newXPath();
    }

    @Override
    public void init() {
        this.read();
    }

    @Override
    public Regex get(String id) {
        try {
            read();
            Node node = (Node) xpath.evaluate("storage/regex[@id=" + id + "]", document, XPathConstants.NODE);
            return map(node);
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public List<Regex> getAll() {
        try {
            read();
            NodeList entities = (NodeList) xpath.evaluate("storage/regex", document, XPathConstants.NODESET);
            List<Regex> list = new ArrayList<>();
            for (int i = 0; i < entities.getLength(); i++) {
                list.add(map(entities.item(i)));
            }
            return list;
        } catch (Exception e) {
            throw new StorageException("Couldn't get entities: " + e.getMessage());
        }
    }

    @Override
    public void store(Regex regex) {
        try {
            read();
            regex.setId(generator.generateId());
            Node root = (Node) xpath.evaluate("/storage", document, XPathConstants.NODE);
            root.appendChild(map(regex));
            save();
        } catch (Exception e) {
            throw new StorageException("Couldn't store regex: " + e.getMessage());
        }
    }

    @Override
    public void update(Regex regex) {
        try {
            read();
            Element node = (Element) xpath.evaluate("storage/regex[@id=" + regex.getId() + "]", document, XPathConstants.NODE);
            setChildNodeTextContent(node, "value", regex.getValue());
            setChildNodeTextContent(node, "description", regex.getDescription());
            setChildNodeTextContent(node, "text", regex.getText());
            save();
        } catch (Exception e) {
            throw new StorageException("Couldn't update regex: " + e.getMessage());
        }
    }

    private void setChildNodeTextContent(Element parent, String childNodeName, String content) {
        parent.getElementsByTagName(childNodeName).item(0).setTextContent(encoded(content));
    }

    @Override
    public void delete(String id) {
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

    private Regex map(Node node) throws XPathExpressionException {
        String id = xpath.evaluate("@id", node);
        String value = decoded(xpath.evaluate("value", node));
        String description = decoded(xpath.evaluate("description", node));
        String text = decoded(xpath.evaluate("text", node));
        return new Regex(id, value, description, text);
    }

    private Node map(Regex regex) {
        Element parent = document.createElement("regex");
        parent.setAttribute("id", regex.getId());
        NodeBuilder nb = new NodeBuilder(parent);
        return nb.append("value", encoded(regex.getValue()))
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
            throw new StorageException("Couldn't read xml database: " + e.getMessage());
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
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    private String decoded(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes()));
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

    interface IdGenerator {
        String generateId();
    }

    class IncrementalIdGenerator implements  IdGenerator {
        public String generateId() {
            try {
                Element root = (Element) xpath.evaluate("/storage", document, XPathConstants.NODE);
                NodeList entities = root.getElementsByTagName("regex");
                if (entities.getLength() == 0) {
                    return "1";
                } else {
                    Element last = (Element) entities.item(entities.getLength() - 1);
                    return Integer.toString(Integer.parseInt(last.getAttribute("id")) + 1);
                }
            } catch (XPathExpressionException e) {
                throw new StorageException("Couldn't generate ID", e);
            }
        }
    }

}
