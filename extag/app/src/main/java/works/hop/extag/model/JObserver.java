package works.hop.extag.model;

import java.util.List;
import java.util.Map;

public interface JObserver {

    Map<String, List<JNode>> observables();

    void subscribe(String subscriber, JNode observable);
}
