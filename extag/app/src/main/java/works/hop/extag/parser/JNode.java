package works.hop.extag.parser;

import java.util.function.Predicate;

public interface JNode {

    default JObserver observer() {
        return null;
    }

    default void observer(JObserver observer) {

    }

    default JNode parent() {
        return null;
    }

    default void parent(JNode parent) {

    }

    default JNode root() {
        JNode prev = this;
        while (prev.parent() != null) {
            prev = prev.parent();
        }
        return prev;
    }

    /**
     * JObject retrieve from a map the value reachable by key
     *
     * @param parent     value of parent node if it exists
     * @param subscriber key to register interest for notification if the value changes
     * @param key        value used as key in the map
     * @return value if key exists else null
     */
    default Object get(JNode parent, String subscriber, String key) {
        return null;
    }

    /**
     * JArray retrieve from the array the value at index
     *
     * @param parent     value of parent node if it exists
     * @param subscriber key to register interest for notification if the value changes
     * @param index      ordinal position of element in array
     * @return value at the index position if it's within valid range
     */
    default Object get(JNode parent, String subscriber, int index) {
        return null;
    }

    /**
     * JArray retrieve from array the first object that matches the predicate
     *
     * @param parent     value of parent node if it exists
     * @param subscriber key to register interest for notification if the value changes
     * @param predicate  test for presence or existence
     * @return value if found else null
     */
    default Object get(JNode parent, String subscriber, Predicate<Object> predicate) {
        return null;
    }

    /**
     * JArray replace in array the value at index
     *
     * @param parent value of parent node if it exists
     * @param index  ordinal position of element to replace in the array
     * @param value  new value intended
     * @return old value at the index position if it's within valid range
     */
    default Object set(JNode parent, int index, Object value) {
        return null;
    }

    /**
     * JArray replace first value in the array which matches predicate
     *
     * @param parent    value of parent node if it exists
     * @param predicate test for presence or existence
     * @param value     value replaced in the array
     */
    default void set(JNode parent, Predicate<Object> predicate, Object value) {

    }

    /**
     * JObject add new value with given key into a map
     *
     * @param parent value of parent node if it exists
     * @param key    value used as key in the map
     * @param value  value put into the map
     */
    default void put(JNode parent, String key, Object value) {

    }

    /**
     * JObject replace value with given key in a map
     *
     * @param parent value of parent node if it exists
     * @param key    value used as key in the map
     * @param value  value replaced in the map
     */
    default void replace(JNode parent, String key, Object value) {

    }

    /**
     * JArray add an element to the array
     *
     * @param parent value of parent node if it exists
     * @param value  value added to the array
     */
    default boolean add(JNode parent, Object value) {
        return false;
    }

    /**
     * JArray remove from the array the value at index
     *
     * @param parent value of parent node if it exists
     * @param index  ordinal position of element in array
     * @return value at the index position if it's within valid range
     */
    default Object remove(JNode parent, int index) {
        return null;
    }

    /**
     * JArray remove from array the first object that matches the predicate
     *
     * @param parent    value of parent node if it exists
     * @param predicate test for presence or existence
     * @return value if found else null
     */
    default Object remove(JNode parent, Predicate<Object> predicate) {
        return null;
    }

    /**
     * JObject remove from a map the value reachable by key
     *
     * @param parent value of parent node if it exists
     * @param key    value used as key in the map
     * @return value if key exists else null
     */
    default Object remove(JNode parent, String key) {
        return null;
    }

    int size();

    boolean isEmpty();

    void clear();
}
