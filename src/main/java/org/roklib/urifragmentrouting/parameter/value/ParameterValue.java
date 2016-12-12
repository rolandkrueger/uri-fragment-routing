package org.roklib.urifragmentrouting.parameter.value;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.UriParameterError;

/**
 * This class is a wrapper around a single URI parameter value. It may contain a valid value or, alternatively,
 * information about an error if no value is available. Instances of this class can be created for two purposes. The
 * first is to transport parameter values captured during the interpretation process of a URI fragment to an action
 * command object. All such captured parameter values are stored in a {@link CapturedParameterValues} object and passed
 * to an action command object if requested.
 * <p>
 * The second purpose is to define the parameter values to be included in a URI fragment programmatically generated with
 * {@link org.roklib.urifragmentrouting.UriActionMapperTree#assembleUriFragment(CapturedParameterValues,
 * UriPathSegmentActionMapper)}.
 * <p>
 * When a URI fragment is interpreted by a {@link org.roklib.urifragmentrouting.UriActionMapperTree} parameter values
 * from the currently interpreted URI fragment are extracted and stored in individual {@link ParameterValue} objects.
 * Usually, a value is expected in the URI fragment to be present for every parameter registered for each path segment
 * mapper found for the URI fragment. If no value is found in the URI fragment for one particular parameter either the
 * parameter's default value is put into a {@link ParameterValue} object or an error represented by {@link
 * UriParameterError}. URI action command objects can then evaluate the {@link ParameterValue} objects, retrieve values
 * and errors from them, and act accordingly.
 * <p>
 * New instance of {@link ParameterValue} are created with one of the static factory methods of this class.
 *
 * @param <V> data type of the parameter value
 */
public class ParameterValue<V> {
    private final V value;
    private final UriParameterError error;
    private boolean isDefault;

    /**
     * Factory method that creates a new {@link ParameterValue} representing the default value of a URI parameter. This
     * factory method is used if a required URI parameter value is not found in an interpreted URI fragment and the
     * corresponding {@link org.roklib.urifragmentrouting.parameter.UriParameter} has a default value defined (i. e. it
     * is optional).
     *
     * @param defaultValue the default value
     * @param <T>          data type of the default value
     * @return a new {@link ParameterValue} object that contains a parameter's default value
     */
    public static <T> ParameterValue<T> forDefaultValue(T defaultValue) {
        final ParameterValue<T> result = new ParameterValue<>(defaultValue);
        result.setIsDefault();
        return result;
    }

    /**
     * Factory method that creates a new {@link ParameterValue} for the given URI parameter value.
     *
     * @param value the parameter value
     * @param <T>   data type of the parameter value
     * @return a new {@link ParameterValue} object that contains a value for a {@link org.roklib.urifragmentrouting.parameter.UriParameter}.
     */
    public static <T> ParameterValue<T> forValue(T value) {
        return new ParameterValue<>(value);
    }

    /**
     * Factory method that creates a new {@link ParameterValue} indicating an error. This factory method is used when a
     * URI fragment is interpreted and no valid value could be found for a required URI parameter. This may be the case
     * when the parameter is not optional and no value is present or when the value could not be converted into the
     * parameter's data type (i. e. a {@link org.roklib.urifragmentrouting.exception.ParameterValueConversionException}
     * was thrown by the corresponding {@link org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter}.
     * The concrete error type can then be queried with {@link #getError()}.
     *
     * @param error the error type indicating the reason for the error
     * @param <T>   data type of the parameter value
     * @return a new {@link ParameterValue} indicating that a given URI parameter could not be successfully captured
     * from the current URI fragment
     * @throws IllegalArgumentException if {@link UriParameterError#NO_ERROR} is used as error type
     * @see UriParameterError
     * @see org.roklib.urifragmentrouting.exception.ParameterValueConversionException
     */
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
     * @throws IllegalStateException when this object contains an error and therefore no parameter value is available
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
     * corresponding parameter threw a {@link org.roklib.urifragmentrouting.exception.ParameterValueConversionException}
     * or when no parameter value could be found for a non-optional URI parameter.
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

    private void setIsDefault() {
        this.isDefault = true;
    }

    @Override
    public String toString() {
        return "ParameterValue{" +
                "value=" + value +
                ", error=" + error +
                ", isDefault=" + isDefault +
                '}';
    }
}
