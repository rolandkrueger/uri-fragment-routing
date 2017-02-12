package org.roklib.urifragmentrouting.exception;

/**
 * Exception which will be thrown when a method of a {@link org.roklib.urifragmentrouting.UriActionCommand
 * UriActionCommand} object which is annotated with one of the annotations from package <tt>annotation</tt> does not
 * adhere to the required format. This may happen, for example, when such a method does not have the expected parameter
 * type or more than one parameter.
 */
public class InvalidMethodSignatureException extends RuntimeException {
    /**
     * Constructs a new exception object with the given message.
     *
     * @param message message object to add to the exception
     */
    public InvalidMethodSignatureException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception object with the given message and cause.
     *
     * @param message message object to add to the exception
     * @param cause   cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code
     *                null} value is permitted. This indicates that the cause is nonexistent or unknown.)
     */
    public InvalidMethodSignatureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
