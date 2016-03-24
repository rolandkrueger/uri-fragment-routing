package org.roklib.urifragmentrouting.exception;

/**
 * Exception which will be thrown when an {@link org.roklib.urifragmentrouting.UriActionCommand} object cannot be
 * successfully instantiated. This may happen, for example, when an action command class does not provide an accessible
 * default constructor, when it is abstract or an interface.
 *
 * @author Roland Krüger
 */
public class InvalidActionCommandClassException extends RuntimeException {
    public InvalidActionCommandClassException(String message) {
        super(message);
    }
}
