package works.hop.emvee.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TemplateProcessor implements JObserver {

    Map<String, List<JNode>> observables = new LinkedHashMap<>();
    Object context;

    public TemplateProcessor(Object context) {
        this.context = context;
    }

    public String process(JElement element) {
        element.setContext(context);
        return element.render();
    }

    @Override
    public Map<String, List<JNode>> observables() {
        return this.observables;
    }

    @Override
    public void subscribe(String subscriber, JNode observable) {

    }
}