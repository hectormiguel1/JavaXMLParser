public class Test {
    public static void main(String[] args) {
        var xml = "<root year=\"220\">\nTest\n<child>\nTest2\n<grandchild>\nTest3\n</grandchild>\n</child>\n</root>";
        var parser = new XMLParser(xml);
        var root = parser.getRoot();
        System.out.println(root.getValue());
    }
}
