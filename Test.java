import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        String xml;
        try {
            xml = new String(Files.readAllBytes(Paths.get("./note.xml")), Charset.defaultCharset());
            var parser = new XMLParser(xml);
            var root = parser.getRoot();
            System.out.println(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
