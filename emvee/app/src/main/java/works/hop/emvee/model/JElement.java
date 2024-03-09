package works.hop.emvee.model;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import java.util.*;

public class JElement extends JObject {

    public static final List<String> selfClosing = List.of("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr");
    protected String tagName;
    protected Object context;
    protected String ifExpression;
    protected String listExpression;
    protected String listItemsKey;
    protected String textExpression;
    protected String textContent;
    protected String templateContent;
    protected boolean isComponent;
    protected boolean isTextNode;
    protected boolean isTemplateNode;
    protected Map<String, String> attributes = new LinkedHashMap<>();
    protected List<JElement> children = new LinkedList<>();

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
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

    public boolean isTemplateNode() {
        return isTemplateNode;
    }

    public void setTemplateNode(boolean templateNode) {
        isTemplateNode = templateNode;
    }

    public JElement[] children() {
        return this.children.toArray(JElement[]::new);
    }

    public String[][] attribute() {
        return this.attributes.entrySet().stream().map(e -> {
            String[] pair = new String[2];
            pair[0] = e.getKey();
            pair[1] = e.getValue();
            return pair;
        }).toArray(String[][]::new);
    }

    public boolean canRender() {
        return ifExpression != null ? (Boolean) MVEL.eval(ifExpression, context) : true;
    }

    public boolean isListElement() {
        return listExpression != null;
    }

    public String templateMarkup() {
        return null;
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
            builder.append(renderNonComponent());
        }
        return builder.toString();
    }

    public String renderComponent() {
        if (ifExpression == null || (Boolean) MVEL.eval(ifExpression, context)) {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(tagName.replaceFirst("x-", ""));
            for (String attr : attributes.keySet()) {
                builder.append(" ").append(attr).append("=").append("\"").append(attributes.get(attr)).append("\"");
            }
            builder.append(">");
            if (textExpression != null) {
                builder.append(MVEL.eval(textExpression, context));
            }
            else if(templateContent != null){
                builder.append(TemplateRuntime.eval(templateContent, context));
            } else {
                for (JElement child : children) {
                    child.setContext(context);
                    builder.append(child.render());
                }
            }
            if (selfClosing.contains(tagName.toLowerCase())) {
                builder.deleteCharAt(builder.length() - 1).append("/>");
            } else {
                builder.append("</").append(tagName.replaceFirst("x-", "")).append(">");
            }
            return builder.toString();
        }
        return "";
    }

    public String renderNonComponent() {
        StringBuilder builder = new StringBuilder();
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
            if (selfClosing.contains(tagName.toLowerCase())) {
                builder.deleteCharAt(builder.length() - 1).append("/>");
            } else {
                builder.append("</").append(tagName).append(">");
            }
        }
        return builder.toString();
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
                if (selfClosing.contains(tagName.toLowerCase())) {
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
