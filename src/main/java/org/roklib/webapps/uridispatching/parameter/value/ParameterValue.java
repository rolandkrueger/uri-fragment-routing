package org.roklib.webapps.uridispatching.parameter.value;

import org.roklib.webapps.uridispatching.helper.Preconditions;

/**
 * Contains a single parameter value which has been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class ParameterValue<V> {
    private V value;

    public ParameterValue(V value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
