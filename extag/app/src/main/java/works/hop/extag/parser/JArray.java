package works.hop.extag.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

public class JArray extends LinkedList<Object> implements JNode {

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
    public Object get(JNode parent, String subscriber, int index) {
        return super.get(index);
    }

    @Override
    public Object get(JNode parent, String subscriber, Predicate<Object> predicate) {
        return super.stream().filter(predicate).findFirst().orElseThrow();
    }

    @Override
    public Object set(JNode parent, int index, Object value) {
        return super.set(index, value);
    }

    @Override
    public void set(JNode parent, Predicate<Object> predicate, Object value) {
        for (int i = 0; i < super.size(); i++) {
            if (predicate.test(super.get(i))) {
                super.set(i, value);
                break;
            }
        }
    }

    @Override
    public boolean add(JNode parent, Object value) {
        return super.add(value);
    }

    @Override
    public Object remove(JNode parent, int index) {
        return super.remove(index);
    }

    @Override
    public Object remove(JNode parent, Predicate<Object> predicate) {
        for (Iterator<Object> iterator = super.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            if (predicate.test(next)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }
}
