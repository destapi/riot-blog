package works.hop.extag.template;

import works.hop.extag.parser.JContext;
import works.hop.extag.parser.JElement;
import works.hop.extag.parser.JParser;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class ShopTemplates {

    public static String resourceFolder = "extag/www";
    public static String[] pages = {"index", "cart", "checkout", "detail", "contact", "shop"};

    public static void main(String[] args) throws XMLStreamException, IOException {
        for(String page : pages){
            JContext processor = new JContext(Map.of("page", Map.of("title", "MultiShop - Online Shop Website Template")));
            JParser parser = new JParser(String.format("/online/%s.xml", page), processor);
            JElement root = parser.parse();
            String markup = processor.process(root);
            Files.writeString(Path.of(resourceFolder, String.format("%s.html", page)), markup,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
