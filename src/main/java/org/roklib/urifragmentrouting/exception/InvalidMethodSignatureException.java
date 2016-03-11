package org.roklib.urifragmentrouting.exception;

/**
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
