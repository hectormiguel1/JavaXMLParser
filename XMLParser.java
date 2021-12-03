import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class XMLParser {
    private XMLNode root;

    public XMLParser(String xml) {
        parse_with_stack(xml);
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

    // private void parse_with_stack(String xml, Stack<String> stack) {
    // var xmlBytes = xml.getBytes();
    // var currentNode = root;
    // for (int i = 0; i < xmlBytes.length;) {
    // String tag = "";
    // HashMap<String, String> attributes = new HashMap<>();
    // String value = "";
    // if (xmlBytes[i] == '<') {
    // StringBuffer sb = new StringBuffer();
    // while( ((char)xmlBytes[i]) != ((char)'>') || ((char)xmlBytes[i]) != ((char)'
    // ') ) {
    // sb.append(((char) xmlBytes[i]));
    // i++;
    // }
    // tag = sb.toString();
    // if (xmlBytes[i] == '>') {
    // if(currentNode == null) {
    // root = new XMLNode(tag, value, null);
    // currentNode = root;
    // } else {
    // var childNode = new XMLNode(tag, value, currentNode);
    // currentNode.addChild(childNode);
    // currentNode = childNode;
    // }
    // stack.push(tag);
    // } else if (xmlBytes[i] == ' ') {
    // StringBuffer sb2 = new StringBuffer();
    // while(xmlBytes[i] != '>') {
    // sb2.append(xmlBytes[i]);
    // i++;
    // }
    // var attribStr = sb2.toString();
    // var attribs = attribStr.split(" ");
    // attributes = new HashMap<String, String>();
    // for (var attrib : attribs) {
    // attributes.put(attrib.split("=")[0], attrib.split("=")[1]);
    // }

    // }
    // } else {
    // StringBuffer sb = new StringBuffer();
    // while(xmlBytes[i] != '<') {
    // sb.append(xmlBytes[i]);
    // i++;
    // }
    // value = sb.toString();
    // currentNode.setValue(value);
    // }
    // }
    // }

    private void parse_with_stack(String xml) {
        xml = xml.replaceAll("\n", "");
        xml = xml.replaceAll("\r", "");
        xml = xml.replaceAll("\t", "");
        xml = xml.trim();
        Stack<String> tagStack = new Stack<>();
        char[] xmlBytes = xml.toCharArray();
        String currentTag = "";
        XMLNode currentNode = root;
        for (int i = 0; i < xmlBytes.length;) {
            if (xmlBytes[i] == '<') {
                // Check if is closing tag
                if (xmlBytes[i + 1] == '/') {
                    currentTag = readStringTillDelimiter(xml.substring(++i, xml.length()), '>');
                    i = i + currentTag.length() + 1;
                    tagStack.pop();
                    currentNode = currentNode.getParent();
                    continue;
                }
                // Read the current tag
                currentTag = readStringTillDelimiter(xml.substring(++i, xml.length()), '>');
                // Advance the current index in xml bytes for the number of characters read
                i = i + currentTag.length() + 1;
                // If current tag is a doc type or comment, skip it
                if (currentTag.contains("?xml") || currentTag.startsWith("!--") || currentTag.startsWith("!DOCTYPE")
                        || currentTag.isEmpty()) {
                    continue;
                }
                tagStack.push(currentTag);
                // Extract attributes from the current tag
                HashMap<String, String> attributes = extractAttributes(currentTag);
                //Remove discovered attributes from the current tag
                if(attributes.size() > 0) {
                    for (var set : attributes.entrySet()) {
                        currentTag = currentTag.replace(set.getKey() + "=" + set.getValue(), "");
                    }
                }
                // Read the value of the tag if it has any.
                String value = readStringTillDelimiter(xml.substring(i, xml.length()), '<');
                // Advancing the index in xml bytes for the number of characters read
                i = i + value.length();
                // Create a new XMLNode with the current tag and value
                var childNode = new XMLNode(currentTag, value, currentNode, attributes);
                // If the current tag is a closing tag
                if (xmlBytes[i + 1] == '/') {
                    var closingTag = readStringTillDelimiter(xml.substring(++i, xml.length()), '>');
                    i = i + closingTag.length() + 1;
                    // Pop the current tag from the stack
                    tagStack.pop();
                    // Add the new node the current node's children
                    currentNode.addChild(childNode);
                } else {
                    // If the current node is null, set the root node to the new node
                    if (currentNode == null) {
                        root = childNode;
                    } else {
                        // Add the new node as a child of the current node
                        currentNode.addChild(childNode);
                    }
                    // Set the current node to the new node
                    currentNode = childNode;
                }

            }
        }
    }

    // Function extracts the xml attibutes from the current tag by splitting the
    // tag.
    private HashMap<String, String> extractAttributes(String currentTag) {
        HashMap<String, String> attributes = new HashMap<>();
        if (currentTag.contains(" ")) {
            String[] attribs = currentTag.split(" ");
            for (var attrib : attribs) {
                if (attrib.split("=").length == 2) {
                    attributes.put(attrib.split("=")[0], attrib.split("=")[1]);
                }
            }
        }
        return attributes;
    }

    // Function will read characters from a char array till a specified charcter is
    // found,
    // The resulting strill will be returned.
    String readStringTillDelimiter(String str, char delimiter) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) {
                return sb.toString();
            }
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }
}
