package org.roklib.urifragmentrouting.parameter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roland Krüger
 */
public final class SingleValuedParameterFactory {
    private final static Set<Class<?>> SUPPORTED_TYPES = new HashSet<>(
            Arrays.asList(String.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class,
                    Boolean.class,
                    Date.class));

    private SingleValuedParameterFactory() {
    }

    public static AbstractSingleUriParameter<?> createUriParameter(String id, Class forType) {
        if (!SUPPORTED_TYPES.contains(forType)) {
            throw new IllegalArgumentException("Class " + forType + " is not supported as single valued URI parameter. " +
                    "Use one of the following classes: " + SUPPORTED_TYPES);
        }

        if (forType == String.class) {
            return new SingleStringUriParameter(id);
        }
        if (forType == Integer.class) {
            return new SingleIntegerUriParameter(id);
        }
        if (forType == Long.class) {
            return new SingleLongUriParameter(id);
        }
        if (forType == Float.class) {
            return new SingleFloatUriParameter(id);
        }
        if (forType == Double.class) {
            return new SingleDoubleUriParameter(id);
        }
        if (forType == Boolean.class) {
            return new SingleBooleanUriParameter(id);
        }
        if (forType == Date.class) {
            return new SingleDateUriParameter(id);
        }

        return null;
    }
}
