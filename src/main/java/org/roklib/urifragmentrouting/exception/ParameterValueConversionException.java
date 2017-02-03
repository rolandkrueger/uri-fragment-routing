package org.roklib.urifragmentrouting.exception;

import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

/**
 * This exception is thrown when the String representation of a URI fragment parameter value could not be successfully
 * converted back into its respective domain type by a {@link ParameterValueConverter}.
 *
 * @see ParameterValueConverter
 */
public class ParameterValueConversionException extends Exception {

    /**
     * Constructs a new exception.
     */
    public ParameterValueConversionException() {
    }

    /**
     * Constructs a new exception with the given message object.
     *
     * @param message message object to add to the exception
     */
    public ParameterValueConversionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception object with the given message and cause.
     *
     * @param message message object to add to the exception
     * @param cause   cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code
     *                null} value is permitted. This indicates that the cause is nonexistent or unknown.)
     */
    public ParameterValueConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
