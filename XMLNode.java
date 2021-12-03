import java.util.ArrayList;
import java.util.HashMap;

class XMLNode {
    private String name;
    private String value;
    private XMLNode parent;
    private ArrayList<XMLNode> children;
    private HashMap<String, String> attributes;

    public XMLNode(String name, String value, XMLNode parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.children = new ArrayList<XMLNode>();
        this.attributes = new HashMap<String, String>();
    }

    public XMLNode(String name, String value) {
        this(name, value, null);
    }

    public XMLNode(String name) {
        this(name, null, null);
    }

    public XMLNode(String name, String value, XMLNode parent, HashMap<String, String> attributes) {
        this(name, value, parent);
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public XMLNode getParent() {
        return parent;
    }

    public ArrayList<XMLNode> getChildren() {
        return children;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setParent(XMLNode parent) {
        this.parent = parent;
    }

    public void setChildren(ArrayList<XMLNode> children) {
        this.children = children;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addChild(XMLNode child) {
        this.children.add(child);
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public void removeChild(XMLNode child) {
        this.children.remove(child);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public void removeAllChildren() {
        this.children.clear();
    }

    public void removeAllAttributes() {
        this.attributes.clear();
    }

    public void removeAll() {
        this.removeAllChildren();
    }

    public void removeEmpty() {
        for (XMLNode child : this.children) {
            child.removeEmpty();
        }
        children.removeIf(XMLNode::isEmpty);
    }

    public boolean isEmpty() {
        return this.name.isEmpty() && this.value.isEmpty() && this.children.size() == 0 && this.attributes.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(this.name);
        if (this.attributes.size() > 0) {
            for (String key : this.attributes.keySet()) {
                sb.append(" ");
                sb.append(key);
                sb.append("=\"");
                sb.append(this.attributes.get(key));
                sb.append("\"");
            }
        }
        if (this.value == null) {
            sb.append("/>");
        } else {
            sb.append(">");
            sb.append(this.value);
            if (this.children.size() > 0) {
                for (XMLNode child : this.children) {
                    sb.append(child.toString());
                }
            }
            sb.append("</");
            sb.append(this.name);
            sb.append(">");
        }
        return sb.toString();
    }

    public XMLNode getNode(String string) {
        if (this.name.equals(string)) {
            return this;
        } else {
            for (XMLNode child : this.children) {
                XMLNode node = child.getNode(string);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
}