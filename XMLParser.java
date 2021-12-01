import java.util.HashMap;
import java.util.Stack;

public class XMLParser {
    private XMLNode root;

    public XMLParser(String xml) {
        parse_with_stack(xml);
    }

    public XMLNode getXMLNode() {
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

    private void parse_with_stack(String xml) {
        Stack<String> stack = new Stack<>();
        String trimmed = xml.trim();
        var chars = trimmed.toCharArray();
        var currentTag = "";
        var currentValue = "";
        var currentNode = root;
        var start = 0;
        while (start < chars.length) {
            if (chars[start] == '<') {
                if (chars[start + 1] == '/') {
                    var end = start + 2;
                    while (end < chars.length && chars[end] != '>') {
                        end++;
                    }
                    var tag = new String(chars, start + 2, end - start - 2);
                    if (stack.isEmpty() || !stack.peek().equals(tag)) {
                        throw new RuntimeException("Invalid XML");
                    }
                    stack.pop();
                    if (currentNode == null) {
                        root = new XMLNode(currentTag, currentValue, null);
                        currentNode = root;
                    } else {
                        if (currentTag.equals(currentNode.getName())) {
                            currentNode.setValue(currentValue);
                        } else {
                            currentNode = currentNode.getParent();
                            if (!currentTag.isEmpty()) {
                                var childNode = new XMLNode(currentTag, currentValue, currentNode);
                                currentNode.addChild(childNode);
                                currentNode = childNode;
                            }
                        }
                    }
                    currentTag = "";
                    currentValue = "";
                    start = end + 1;
                } else {
                    var end = start + 1;
                    while (end < chars.length && chars[end] != '>') {
                        end++;
                    }
                    var tag = new String(chars, start + 1, end - start - 1);
                    if (tag.contains("?xml") || tag.contains("!DOCTYPE") || tag.contains("!--")) {
                        start = end + 1;
                        continue;
                    }
                    if (currentNode == null) {
                        root = new XMLNode(tag, "", null);
                        currentNode = root;
                    } else {
                        var childNode = new XMLNode(tag, "", currentNode);
                        currentNode.addChild(childNode);
                        currentNode = childNode;
                    }
                    stack.push(tag);
                    currentTag = tag;
                    currentValue = "";
                    start = end + 1;
                }
            } else {
                var end = start + 1;
                while (end < chars.length && chars[end] != '<') {
                    end++;
                }
                var value = new String(chars, start, end - start);
                currentValue += value;
                start = end;
            }
        }
    }
}
