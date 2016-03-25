package org.roklib.urifragmentrouting.parameter.converter;

/**
 * This exception is thrown when the String representation of a URI fragment parameter value could not be successfully
 * converted back into its respective domain type.
 *
 * @author Roland Krüger
 * @see ParameterValueConverter
 */
public class ParameterValueConversionException extends Exception {
    public ParameterValueConversionException() {
    }

    public ParameterValueConversionException(String message) {
        super(message);
    }

    public ParameterValueConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterValueConversionException(Throwable cause) {
        super(cause);
    }

    public ParameterValueConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
