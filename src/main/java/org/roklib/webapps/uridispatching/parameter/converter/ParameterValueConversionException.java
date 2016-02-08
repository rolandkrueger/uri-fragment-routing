package org.roklib.webapps.uridispatching.parameter.converter;

/**
 * @author Roland Krüger
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
