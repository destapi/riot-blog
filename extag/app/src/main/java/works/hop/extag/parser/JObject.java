package works.hop.extag.parser;

import java.util.LinkedHashMap;

public class JObject extends LinkedHashMap<String, Object> implements JNode {

    JObserver observer;
    JNode parent;

    @Override
    public JObserver observer() {
        return this.observer;
    }

    @Override
    public void observer(JObserver observer) {
        this.observer = observer;
    }

    @Override
    public JNode parent() {
        return this.parent;
    }

    @Override
    public void parent(JNode parent) {
        this.parent = parent;
    }

    @Override
    public Object get(JNode parent, String subscriber, String key) {
        return super.get(key);
    }

    @Override
    public void put(JNode parent, String key, Object value) {
        super.put(key, value);
    }

    @Override
    public void replace(JNode parent, String key, Object value) {
        super.replace(key, value);
    }

    @Override
    public Object remove(JNode parent, String key) {
        return super.remove(key);
    }
}
