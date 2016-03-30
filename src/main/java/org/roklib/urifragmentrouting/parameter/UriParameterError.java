package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

/**
 * Error type which describes error situations when capturing URI parameter values from a URI fragment. The result of
 * this capturing process is stored in an object of type {@link org.roklib.urifragmentrouting.parameter.value.ParameterValue}.
 * If an error occurred while interpreting a URI parameter the error type can be queried with {@link
 * ParameterValue#getError()}.
 */
public enum UriParameterError {
    /**
     * Indicates that a parameter value could be extracted without an error. In this case, the value can be obtained
     * with {@link ParameterValue#getValue()}.
     */
    NO_ERROR,
    /**
     * Indicates that no value could be captured from the URI fragment for a non-optional parameter.
     */
    PARAMETER_NOT_FOUND,
    /**
     * Indicates that a value is present in the URI fragment for the URI parameter but it could not be successfully
     * converted into the domain type of the parameter.
     *
     * @see org.roklib.urifragmentrouting.exception.ParameterValueConversionException
     */
    CONVERSION_ERROR
}
