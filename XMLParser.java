public class XMLParser {
    private XMLNode root;

    public XMLParser(String xml) {
        parse(xml);
    }

    public XMLNode getRoot() {
        return root;
    }

    private void parse(String xml) {
        String[] lines = xml.split("\n");
        String currentTag = null;
        XMLNode currentNode = root;
        for (String line : lines) {
            if (line.contains("<?xml") || line.contains("<!DOCTYPE") || line.contains("<!--")) {
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
}
