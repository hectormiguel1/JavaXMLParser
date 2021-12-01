import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class XMLParser {
    private XMLNode root;

    public XMLParser(String xml) {
        parse_with_stack(xml, new Stack<String>());
    }

    public XMLNode getRoot() {
        return root;
    }

    private void parse(String xml) {
        String[] lines = xml.split("\n");
        String currentTag = null;
        XMLNode currentNode = root;
        for (String line : lines) {
            if (line.contains("<?xml") || line.contains("<!DOCTYPE") || line.contains("<!--") || line.isEmpty()) {
                continue;
            }
            if (line.startsWith("<")) {
                if (line.startsWith("</")) {
                    currentNode = currentNode.getParent();
                } else {
                    currentTag = line.substring(1, line.indexOf('>'));
                    if (currentNode == null) {
                        var childNode = new XMLNode(currentTag, "", null);
                        root = childNode;
                        currentNode = root;
                    } else {
                        var childNode = new XMLNode(currentTag, "", currentNode);
                        currentNode.addChild(childNode);
                        currentNode = childNode;
                    }
                }
            } else {
                currentNode.setValue(line);
            }
        }
    }

    private void parse_with_stack(String xml, Stack<String> stack) {
        var xmlBytes  = xml.getBytes();
        var currentNode = root;
        for (int i = 0; i < xmlBytes.length;) {
            String tag = "";
            HashMap<String, String> attributes = new HashMap<>();
            String value = "";
            if (xmlBytes[i] == '<') {
               StringBuffer sb = new StringBuffer();
                while( ((char)xmlBytes[i]) != ((char)'>') || ((char)xmlBytes[i]) != ((char)' ') ) {
                    sb.append(((char) xmlBytes[i]));
                    i++;
                }
                tag = sb.toString();
                if (xmlBytes[i] == '>') {
                    if(currentNode == null) {
                        root = new XMLNode(tag, value, null);
                        currentNode = root;
                    } else {
                        var childNode = new XMLNode(tag, value, currentNode);
                        currentNode.addChild(childNode);
                        currentNode = childNode;
                    }
                    stack.push(tag);
                } else if (xmlBytes[i] == ' ') {
                    StringBuffer sb2 = new StringBuffer();
                    while(xmlBytes[i] != '>') {
                        sb2.append(xmlBytes[i]);
                        i++;
                    }
                    var attribStr = sb2.toString();
                    var attribs = attribStr.split(" ");
                    attributes = new HashMap<String, String>();
                    for (var attrib : attribs) {
                        attributes.put(attrib.split("=")[0], attrib.split("=")[1]);
                    }
                    
                }
            } else {
                StringBuffer sb = new StringBuffer();
                while(xmlBytes[i] != '<') {
                    sb.append(xmlBytes[i]);
                    i++;
                }
                value = sb.toString();
                currentNode.setValue(value);
            }
        }
    }
}
