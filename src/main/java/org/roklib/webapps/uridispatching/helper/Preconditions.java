package org.roklib.webapps.uridispatching.helper;

public final class Preconditions {
    public static void checkNotNull(Object argument) {
        if (argument == null) {
            throw new NullPointerException();
        }
    }
}
