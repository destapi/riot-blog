package works.hop.emvee.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TemplateParserTest {

    @Test
    void test_loading_and_parsing_websites_xml() {
        String file = "/websites.xml";
        TemplateProcessor processor = new TemplateProcessor(emptyMap());
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        System.out.println(processor.process(root));
    }

    @Test
    void test_loading_and_parsing_template_tags_xml() {
        String file = "/template-tags.xml";
        TemplateProcessor processor = new TemplateProcessor(Map.of("name", "Cassie", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(3);
        assertThat(processor.process(root)).isEqualTo("<p class=\"bg-dark\"><span class=\"title\">Cassie</span><i class=\"fa fa-check\"></i></p>");
    }

    @Test
    void test_loading_and_parsing_template_tags_2_xml() {
        String file = "/template-tags2.xml";
        TemplateProcessor processor = new TemplateProcessor(Map.of("name", "Jimbob", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(2);
        assertThat(processor.process(root)).isEqualTo("<p class=\"fa fa-check\">Yepee<span class=\"title\">Jimbob</span></p>");
    }

    @Test
    void test_loading_and_parsing_plain_tags_xml() {
        String file = "/plain-tags.xml";
        TemplateProcessor processor = new TemplateProcessor(emptyMap());
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(2);
        assertThat(processor.process(root)).isEqualTo("<p id=\"name\"><span class=\"title\">Jimmy</span><i class=\"fa fa-check\"></i></p>");
    }

    @Test
    void test_loading_and_parsing_todolist_xml() {
        String file = "/todo-list.xml";
        TemplateProcessor processor = new TemplateProcessor(Map.of("items",
                List.of(Map.of("id", "1", "title", "Read book", "done", true),
                        Map.of("id", 2, "title", "Make pancakes", "done", false))));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(3);
        String markup = processor.process(root);
        System.out.println(markup);
        assertThat(markup).isEqualTo("<div id=\"todo-list\"><form onsubmit=\"add\"><label><input name=\"title\" onchange=\"edit\"/></label><button type=\"submit\">Add</button></form><ul><li><i title=\"done\" class=\"fa fa-square\"></i><span>Read book</span><i title=\"remove\" class=\"fa fa-times-circle\"></i></li><li><i title=\"done\" class=\"fa fa-check-square\"></i><span>Make pancakes</span><i title=\"remove\" class=\"fa fa-times-circle\"></i></li></ul><p>2</p></div>");
    }
}