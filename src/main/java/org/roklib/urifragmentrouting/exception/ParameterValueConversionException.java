package org.roklib.urifragmentrouting.exception;

import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

/**
 * This exception is thrown when the String representation of a URI fragment parameter value could not be successfully
 * converted back into its respective domain type by a {@link ParameterValueConverter}.
 *
 * @see ParameterValueConverter
 */
public class ParameterValueConversionException extends Exception {
    public ParameterValueConversionException() {
    }

    public ParameterValueConversionException(final String message) {
        super(message);
    }

    public ParameterValueConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
