package org.roklib.webapps.uridispatching.parameter.value;

/**
 * Contains a single parameter value which has been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class ParameterValue<V> {
    private V value;

    public ParameterValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
