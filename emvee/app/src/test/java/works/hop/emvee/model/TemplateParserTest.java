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
        JContext processor = new JContext(emptyMap());
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        System.out.println(processor.process(root));
    }

    @Test
    void test_loading_and_parsing_template_tags_xml() {
        String file = "/template-tags.xml";
        JContext processor = new JContext(Map.of("name", "Cassie", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(3);
        assertThat(processor.process(root)).isEqualTo("<p class=\"bg-dark\"><span class=\"title\">Cassie</span><i class=\"fa fa-check\"></i></p>");
    }

    @Test
    void test_loading_and_parsing_template_tags_2_xml() {
        String file = "/template-tags2.xml";
        JContext processor = new JContext(Map.of("name", "Jimbob", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(2);
        assertThat(processor.process(root)).isEqualTo("<p class=\"fa fa-check\">Yepee<span class=\"title\">Jimbob</span></p>");
    }

    @Test
    void test_loading_and_parsing_template_tags_3_xml() {
        String file = "/template-tags3.xml";
        JContext processor = new JContext(Map.of("name", "Jimbob", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(0);
        assertThat(processor.process(root)).isEqualTo("<p class=\"fa fa-memo\">My name is Jimbob</p>");
    }

    @Test
    void test_loading_and_parsing_template_tags_4_xml() {
        String file = "/template-tags4.xml";
        JContext processor = new JContext(Map.of("name", "Jimbob", "visible", true, "over", false));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(1);
        assertThat(processor.process(root)).isEqualTo("<p class=\"me\"><p class=\"fa fa-memo\">My name is Jimbob</p></p>");
    }

    @Test
    void test_loading_and_parsing_layout_template_xml() {
        String file = "/layout-template.xml";
        JContext processor = new JContext(emptyMap());
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(2);
        assertThat(root.slots).hasSize(3);
        assertThat(processor.process(root)).isEqualTo("<html lang=\"en\"><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"/><meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\"/><title>Default Title</title></head><body><nav><menu><li>Login</li></menu></nav><main></main><footer><div><span class=\"sticky\">&copy; 2024 ExTag</span></div></footer><script src=\"special-sauce.js\" type=\"module\"></script></body></html>");
    }

    @Test
    void test_loading_and_parsing_page_decorated_with_layout_xml() {
        String file = "/decorated-page.xml";
        JContext processor = new JContext(Map.of("name", "Jimbob", "visible", true, "over", false,
                "page", Map.of("title", "The best title"),
                "items",
                List.of(Map.of("id", "1", "title", "Read book", "done", true),
                        Map.of("id", 2, "title", "Make pancakes", "done", false))));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(6);
        assertThat(processor.process(root)).isEqualTo("<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"/><meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\"/><meta hobbby=\"reading\"/><link rel=\"stylesheet\" hre=\"css/style.css\" type=\"text/css\"/><title>The best title</title><script defer=\"true\" src=\"/js/sauce.js\"></script></head><body><nav><menu><li>Login</li></menu></nav><div id=\"todo-list\"><form onsubmit=\"add\"><label><input name=\"title\" onchange=\"edit\"/></label><button type=\"submit\">Add</button></form><ul><li><i title=\"done\" class=\"fa fa-square\"></i><span>Read book</span><i title=\"remove\" class=\"fa fa-times-circle\"></i></li><li><i title=\"done\" class=\"fa fa-check-square\"></i><span>Make pancakes</span><i title=\"remove\" class=\"fa fa-times-circle\"></i></li></ul><p>2</p></div><footer><div><span class=\"sticky\">&copy; 2024 ExTag</span></div></footer><script src=\"special-sauce.js\" type=\"module\"></script></body></html>");
    }

    @Test
    void test_loading_and_parsing_page_decorated_with_basic_layout_xml() {
        String file = "/basic-page.xml";
        JContext processor = new JContext(Map.of("page", Map.of("title", "The best title")));
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(9);
        assertThat(processor.process(root)).isEqualTo("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"/><meta another=\"something\" hobbby=\"programming\"/><meta skylight=\"azure\" baseline=\"yellow\"/><link rel=\"stylesheet\" hre=\"css/reset.css\" type=\"text/css\"/><link rel=\"stylesheet\" hre=\"css/style.css\" type=\"text/css\"/><title>The best title</title><script defer=\"true\" src=\"/js/hot-sauce.js\"></script><script defer=\"true\" src=\"/js/sweet-sauce.js\"></script></head><body><div id=\"todo-list\">I'm here</div></body></html>");
    }

    @Test
    void test_loading_and_parsing_plain_tags_xml() {
        String file = "/plain-tags.xml";
        JContext processor = new JContext(emptyMap());
        TemplateParser parser = new TemplateParser(file, processor);
        JElement root = assertDoesNotThrow(parser::parse, "Not expecting error to be thrown");
        assertThat(root.children).hasSize(2);
        assertThat(processor.process(root)).isEqualTo("<p id=\"name\"><span class=\"title\">Jimmy</span><i class=\"fa fa-check\"></i></p>");
    }

    @Test
    void test_loading_and_parsing_todolist_xml() {
        String file = "/todo-list.xml";
        JContext processor = new JContext(Map.of("items",
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