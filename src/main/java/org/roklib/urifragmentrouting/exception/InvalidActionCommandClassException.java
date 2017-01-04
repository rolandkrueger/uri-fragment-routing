package org.roklib.urifragmentrouting.exception;

/**
 * Exception which will be thrown when an {@link org.roklib.urifragmentrouting.UriActionCommand} object cannot be
 * successfully instantiated. This may happen, for example, when an action command class does not provide an accessible
 * default constructor, when it is abstract or an interface.
 */
public class InvalidActionCommandClassException extends RuntimeException {
    /**
     * Create a new exception object.
     *
     * @param message message object to add to the exception
     */
    public InvalidActionCommandClassException(final String message) {
        super(message);
    }
}
