public class Test {
    public static void main(String[] args) {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>";
        var parser = new XMLParser(xml);
        var root = parser.getXMLNode();
        System.out.println(root);
    }
}
