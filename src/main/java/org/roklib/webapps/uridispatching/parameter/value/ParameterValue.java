package org.roklib.webapps.uridispatching.parameter.value;

/**
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
