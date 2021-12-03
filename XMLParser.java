import java.util.HashMap;
import java.util.Stack;

public class XMLParser {

    public XMLNode parse(String xml) throws InvalidXMLException {
        // Cleanup incomring xml string
        xml = xml.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").trim();
        // Create stack to keep track of xml tags
        Stack<String> tagStack = new Stack<>();
        XMLNode root = null;
        // get all the characters in the xml string
        char[] xmlBytes = xml.toCharArray();
        String currentTag = "";
        XMLNode currentNode = root;
        // iterate through the characters
        for (int i = 0; i < xmlBytes.length;) {
            // Check of tags
            if (xmlBytes[i] == '<') {
                // Check if is closing tag
                if (xmlBytes[i + 1] == '/') {
                    //Read tag 
                    var closingTag = readStringTillDelimiter(xml.substring(++i, xml.length()), '>');
                    //Increment i by number of chars red
                    i = i + closingTag.length() + 1;
                    //Remove '/' from tag
                    closingTag = closingTag.replaceAll("/", "");
                    // Check if tag is in stack
                    if (!closingTag.equals(tagStack.peek())) {
                        throw new InvalidXMLException("Invalid XML: Closing tag does not match opening tag, closing tag: " + closingTag + " open tag: " + tagStack.peek());
                    }
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
                // Extract attributes from the current tag
                HashMap<String, String> attributes = extractAttributes(currentTag);
                // Remove discovered attributes from the current tag
                if (attributes.size() > 0) {
                    for (var set : attributes.entrySet()) {
                        currentTag = currentTag.replace(set.getKey() + "=" + set.getValue(), "").trim();
                    }
                }
                //Push tag to stack
                tagStack.push(currentTag);
                // Read the value of the tag if it has any.
                String value = readStringTillDelimiter(xml.substring(i, xml.length()), '<').trim();
                // Advancing the index in xml bytes for the number of characters read
                i = i + value.length();
                // Create a new XMLNode with the current tag and value
                var childNode = new XMLNode(currentTag, value, currentNode, attributes);
                // If the current tag is a closing tag
                if (xmlBytes[i + 1] == '/') {
                    // Read the closing tag
                    var closingTag = readStringTillDelimiter(xml.substring(++i, xml.length()), '>');
                    // Advance the current index in xml bytes for the number of characters read
                    i = i + closingTag.length() + 1;

                    // Remove '/' from the current tag
                    closingTag = closingTag.replaceAll("/", "");

                    // Check if the closing tag is the top of the stack
                    if (!closingTag.equals(tagStack.peek())) {
                        throw new InvalidXMLException("Invalid XML: Closing tag does not match opening tag, closing tag: " + closingTag + " open tag: " + tagStack.peek());
                    }
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

            } else {
                var spaces = readStringTillDelimiter(xml.substring(i, xml.length()), '<');//No tags found, prob in a space, advance index
                var space_lenght = spaces.length() ;
                i = i + space_lenght;
            }
        }
        return root;
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
    private String readStringTillDelimiter(String str, char delimiter) {
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
