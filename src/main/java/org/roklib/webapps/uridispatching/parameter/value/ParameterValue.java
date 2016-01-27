package org.roklib.webapps.uridispatching.parameter.value;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.URIParameterError;

/**
 * Contains a single parameter value which has been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class ParameterValue<V> {
    private final V value;
    private final URIParameterError error;

    public ParameterValue(V value) {
        Preconditions.checkNotNull(value);
        this.value = value;
        error = URIParameterError.NO_ERROR;
    }

    public ParameterValue(URIParameterError error) {
        if (error == URIParameterError.NO_ERROR) {
            throw new IllegalArgumentException("Error condition NO_ERROR must not be set explicitly.");
        }
        this.error = error;
        value = null;
    }

    public V getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean hasError() {
        return error != URIParameterError.NO_ERROR;
    }

    public URIParameterError getError() {
        return error;
    }
}
