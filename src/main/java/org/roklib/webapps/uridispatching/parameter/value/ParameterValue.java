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
    private boolean isDefault;

    public static <T> ParameterValue<T> forDefaultValue(T defaultValue) {
        final ParameterValue<T> result = new ParameterValue<>(defaultValue);
        result.setIsDefault(true);
        return result;
    }

    public static <T> ParameterValue<T> forValue(T value) {
        return new ParameterValue<>(value);
    }

    public static <T> ParameterValue<T> forError(URIParameterError error) {
        return new ParameterValue<>(error);
    }

    private ParameterValue(V value) {
        Preconditions.checkNotNull(value);
        this.value = value;
        error = URIParameterError.NO_ERROR;
    }

    private ParameterValue(URIParameterError error) {
        if (error == URIParameterError.NO_ERROR) {
            throw new IllegalArgumentException("Error condition NO_ERROR must not be set explicitly.");
        }
        this.error = error;
        value = null;
    }

    public V getValue() {
        if (hasError()) {
            throw new IllegalStateException("this parameter has an error");
        }
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean isDefaultValue() {
        return isDefault;
    }

    public boolean hasError() {
        return error != URIParameterError.NO_ERROR;
    }

    public URIParameterError getError() {
        return error;
    }

    private void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
