package org.roklib.urifragmentrouting.exception;

/**
 * Exception which will be thrown when a method of a {@link org.roklib.urifragmentrouting.UriActionCommand} object which
 * is annotated with one of the annotations from package <tt>annotation</tt> does not adhere to the required format. This
 * may happen, for example, when such a method does not have the expected parameter type or more than one parameter.
 *
 * @author Roland Krüger
 */
public class InvalidMethodSignatureException extends RuntimeException {
    public InvalidMethodSignatureException(String message) {
        super(message);
    }

    public InvalidMethodSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
