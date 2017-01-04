package org.roklib.urifragmentrouting.helper;

/**
 * Helper class for writing null checks.
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Checks if the given argument is null and throws a {@link NullPointerException} if this is the case.
     */
    public static void checkNotNull(final Object argument) {
        if (argument == null) {
            throw new NullPointerException();
        }
    }
}
