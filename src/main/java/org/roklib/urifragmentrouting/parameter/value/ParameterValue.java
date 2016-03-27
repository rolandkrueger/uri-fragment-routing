package org.roklib.urifragmentrouting.parameter.value;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.UriParameterError;

/**
 * Contains a single parameter value which has been captured during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class ParameterValue<V> {
    private final V value;
    private final UriParameterError error;
    private boolean isDefault;

    /**
     * Factory method that creates a new {@link ParameterValue} which represents the default value of a URI parameter.
     * Use this method if
     *
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> ParameterValue<T> forDefaultValue(T defaultValue) {
        final ParameterValue<T> result = new ParameterValue<>(defaultValue);
        result.setIsDefault(true);
        return result;
    }

    public static <T> ParameterValue<T> forValue(T value) {
        return new ParameterValue<>(value);
    }

    public static <T> ParameterValue<T> forError(UriParameterError error) {
        return new ParameterValue<>(error);
    }

    private ParameterValue(V value) {
        Preconditions.checkNotNull(value);
        this.value = value;
        error = UriParameterError.NO_ERROR;
    }

    private ParameterValue(UriParameterError error) {
        if (error == UriParameterError.NO_ERROR) {
            throw new IllegalArgumentException("Error condition NO_ERROR must not be set explicitly.");
        }
        this.error = error;
        value = null;
    }

    /**
     * Returns the captured parameter value. Check with {@link #hasValue()} whether a value is available.
     *
     * @throws IllegalStateException when this object contains an error and no parameter value is available
     */
    public V getValue() {
        if (hasError()) {
            throw new IllegalStateException("this parameter has an error");
        }
        return value;
    }

    /**
     * Returns true if this {@link ParameterValue} contains a valid value which can be retrieved with {@link
     * #getValue()}.
     */
    public boolean hasValue() {
        return value != null;
    }

    /**
     * Returns true if the value obtained by {@link #getValue()} is the default value as defined with {@link
     * #forDefaultValue(Object)}.
     */
    public boolean isDefaultValue() {
        return isDefault;
    }

    /**
     * Returns true if this {@link ParameterValue} does not contain a valid value but instead represents an error. This
     * may happen when the {@link org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter} for the
     * corresponding parameter threw a {@link org.roklib.urifragmentrouting.exception.ParameterValueConversionException}.
     */
    public boolean hasError() {
        return error != UriParameterError.NO_ERROR;
    }

    /**
     * If this {@link ParameterValue} represents an error the type of the error can be obtained with this method.
     *
     * @return the type of error that this {@link ParameterValue} represents.
     */
    public UriParameterError getError() {
        return error;
    }

    private void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
