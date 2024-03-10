package works.hop.extag.parser;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import javax.xml.stream.XMLStreamException;
import java.util.*;

public class JElement extends JObject {

    public static final List<String> selfClosingTags = List.of("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr");
    public static final List<String> decoratorTags = List.of("doctype", "meta", "link", "style", "script");
    protected String tagName;
    protected String slotName;
    protected String slotRef;
    protected Object context;
    protected String ifExpression;
    protected String listExpression;
    protected String listItemsKey;
    protected String textExpression;
    protected String textContent;
    protected String evalContent;
    protected String includePath;
    protected String templatePath;
    protected String docTypeTag;
    protected JElement templateElement;
    protected boolean isComponent;
    protected boolean isTextNode;
    protected boolean isEvalNode;
    protected boolean isLayoutSlot;
    protected boolean isSlotNode;
    protected boolean isLayoutNode;
    protected boolean isDecoratorNode;
    protected JElement docTypeElement;
    protected Map<String, String> attributes = new LinkedHashMap<>();
    protected Map<String, JElement> slots = new LinkedHashMap<>();
    protected Map<String, List<JElement>> decorators = new HashMap<>() {
        {
            put("script", new LinkedList<>());
            put("link", new LinkedList<>());
            put("meta", new LinkedList<>());
        }
    };
    protected List<JElement> children = new LinkedList<>();

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotRef() {
        return slotRef;
    }

    public void setSlotRef(String slotRef) {
        this.slotRef = slotRef;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getIfExpression() {
        return ifExpression;
    }

    public void setIfExpression(String ifExpression) {
        this.ifExpression = ifExpression;
    }

    public String getListExpression() {
        return listExpression;
    }

    public void setListExpression(String listExpression) {
        this.listExpression = listExpression;
    }

    public String getListItemsKey() {
        return listItemsKey;
    }

    public void setListItemsKey(String listItemsKey) {
        this.listItemsKey = listItemsKey;
    }

    public String getTextExpression() {
        return textExpression;
    }

    public void setTextExpression(String textExpression) {
        this.textExpression = textExpression;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getEvalContent() {
        return evalContent;
    }

    public void setEvalContent(String evalContent) {
        this.evalContent = evalContent;
    }

    public String getDocTypeTag() {
        return docTypeTag;
    }

    public void setDocTypeTag(String docTypeTag) {
        this.docTypeTag = docTypeTag;
    }

    public boolean isComponent() {
        return isComponent;
    }

    public void setComponent(boolean component) {
        isComponent = component;
    }

    public boolean isTextNode() {
        return isTextNode;
    }

    public void setTextNode(boolean textNode) {
        isTextNode = textNode;
    }

    public boolean isEvalNode() {
        return isEvalNode;
    }

    public void setEvalNode(boolean evalNode) {
        isEvalNode = evalNode;
    }

    public String getIncludePath() {
        return includePath;
    }

    public void setIncludePath(String includePath) {
        this.includePath = includePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public JElement getTemplateElement() {
        return templateElement;
    }

    public void setTemplateElement(JElement templateElement) {
        this.templateElement = templateElement;
    }

    public boolean isLayoutSlot() {
        return isLayoutSlot;
    }

    public void setLayoutSlot(boolean layoutSlot) {
        isLayoutSlot = layoutSlot;
    }

    public boolean isSlotNode() {
        return isSlotNode;
    }

    public void setSlotNode(boolean slotNode) {
        isSlotNode = slotNode;
    }

    public boolean isLayoutNode() {
        return isLayoutNode;
    }

    public void setLayoutNode(boolean layoutNode) {
        isLayoutNode = layoutNode;
    }

    public boolean isDecoratorNode() {
        return isDecoratorNode;
    }

    public void setDecoratorNode(boolean decoratorNode) {
        isDecoratorNode = decoratorNode;
    }

    public JElement getDocTypeElement() {
        return docTypeElement;
    }

    public void setDocTypeElement(JElement docTypeElement) {
        this.docTypeElement = docTypeElement;
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        if (isComponent) {
            if (listExpression == null) {
                builder.append(renderComponent());
            } else {
                builder.append(renderListComponent());
            }
        } else {
            renderNonComponent(builder);
        }
        return builder.toString();
    }

    public void renderLayout(StringBuilder builder) {
        try {
            JContext templateProcessor = new JContext(context);
            JParser parser = new JParser(templatePath, templateProcessor);
            JElement templateRoot = parser.parse();
            setTemplateElement(templateRoot);

            for (JElement child : children) {
                child.setContext(context);
                //delay rendering until when the slot in the template is reached
                if (child.isSlotNode && templateRoot.slots.containsKey(child.slotName)) {
                    templateRoot.slots.put(child.slotName, child);
                    continue;
                }
                String tagName = child.tagName.replaceFirst("x-", "");
                if (decoratorTags.contains(tagName)) {
                    if (tagName.equals("doctype")) {
                        templateRoot.setDocTypeElement(child);
                    } else {
                        child.setDecoratorNode(true);
                        templateRoot.decorators.get(tagName).add(child);
                    }
                }
            }

            if (((JElement) root()).docTypeTag != null) {
                builder.append(((JElement) root()).docTypeTag);
            }

            builder.append(templateProcessor.process(templateRoot));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public String renderComponent() {
        if (ifExpression == null || (Boolean) MVEL.eval(ifExpression, context)) {
            StringBuilder builder = new StringBuilder();
            if (tagName.equals("x-layout")) {
                renderLayout(builder);
            } else if (includePath != null) {
                renderIncluded(builder);
            } else if (isLayoutSlot) {
                setLayoutSlot(false);
                builder.append(((JElement) root()).slots.get(getSlotRef()).render());
            } else if (decoratorTags.contains(tagName.replaceFirst("x-", "")) && !isDecoratorNode()) {
                renderDecorator(builder);
            } else {
                renderTag(builder);
            }
            return builder.toString();
        }
        return "";
    }

    public void renderTag(StringBuilder builder) {
        builder.append("<").append(tagName.replaceFirst("x-", ""));
        for (String attr : attributes.keySet()) {
            builder.append(" ").append(attr).append("=").append("\"").append(attributes.get(attr)).append("\"");
        }
        builder.append(">");
        if (textExpression != null) {
            builder.append(MVEL.eval(textExpression, context));
        } else if (evalContent != null) {
            builder.append(TemplateRuntime.eval(evalContent, context));
        } else {
            for (JElement child : children) {
                child.setContext(context);
                builder.append(child.render());
            }
        }
        if (selfClosingTags.contains(tagName.toLowerCase().replaceFirst("x-", ""))) {
            builder.deleteCharAt(builder.length() - 1).append("/>");
        } else {
            builder.append("</").append(tagName.replaceFirst("x-", "")).append(">");
        }
    }

    public void renderDecorator(StringBuilder builder) {
        switch (tagName) {
            case "x-meta" -> {
                for (JElement meta : ((JElement) root()).decorators.get("meta")) {
                    builder.append(meta.renderComponent());
                }
            }
            case "x-link" -> {
                for (JElement link : ((JElement) root()).decorators.get("link")) {
                    builder.append(link.render());
                }
            }
            case "x-script" -> {
                for (JElement script : ((JElement) root()).decorators.get("script")) {
                    builder.append(script.render());
                }
            }
            default -> throw new RuntimeException("Unsupported decorator tag - '" + tagName + "'");
        }
    }

    public void renderIncluded(StringBuilder builder) {
        try {
            JContext includeProcessor = new JContext(context);
            JParser parser = new JParser(includePath, includeProcessor);
            JElement includeRoot = parser.parse();
            builder.append(includeProcessor.process(includeRoot));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public void renderNonComponent(StringBuilder builder) {
        if (isTextNode()) {
            builder.append(textContent);
        } else {
            builder.append("<").append(tagName);
            for (String attr : attributes.keySet()) {
                builder.append(" ").append(attr).append("=").append("\"").append(attributes.get(attr)).append("\"");
            }
            builder.append(">");
            for (JElement child : children) {
                child.setContext(context);
                builder.append(child.render());
            }
            if (selfClosingTags.contains(tagName.toLowerCase())) {
                builder.deleteCharAt(builder.length() - 1).append("/>");
            } else {
                builder.append("</").append(tagName).append(">");
            }
        }
    }

    public String renderListComponent() {
        if (ifExpression == null || (Boolean) MVEL.eval(ifExpression, context)) {
            StringBuilder builder = new StringBuilder();
            if (isTextNode()) {
                builder.append(textContent);
            } else {
                builder.append("<").append(tagName.replaceFirst("x-", "")).append(">");
                Collection<Object> collection = (Collection<Object>) MVEL.eval(listExpression, context);
                for (Object item : collection) {
                    for (JElement child : children) {
                        child.setContext(item);
                        builder.append(child.render());
                    }
                }
                if (selfClosingTags.contains(tagName.toLowerCase())) {
                    builder.deleteCharAt(builder.length() - 1).append("/>");
                } else {
                    builder.append("</").append(tagName.replaceFirst("x-", "")).append(">");
                }
            }
            return builder.toString();
        }
        return "";
    }
}
