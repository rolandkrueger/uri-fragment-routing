package org.roklib.urifragmentrouting.parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory class for creating instances of single-valued URI parameters on the basis of the parameter value domain type.
 * This factory can create URI parameter objects for the following domain types: <ul> <li>Integer</li> <li>Long</li>
 * <li>Float</li> <li>Double</li> <li>Boolean</li> <li>java.util.Date</li> <li>java.time.LocalDate</li> </ul> This
 * factory is used by the {@link org.roklib.urifragmentrouting.UriActionMapperTree.UriActionMapperTreeBuilder} which
 * allows to add single-valued parameters to an URI path segment mapper by specifying the parameter's type with
 * <code>forType(Class)</code>as in the following example:
 * <p>
 * <pre>
 *  UriActionMapperTree.create().buildMapperTree()
 *      .map("profile").onAction(MyActionCommand.class)
 *      .withSingleValuedParameter("userId").<b>forType(Long.class)</b>.noDefault()
 *      .finishMapper(mappers::put)
 *      .build();
 * </pre>
 */
public final class SingleValuedParameterFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SingleValuedParameterFactory.class);

    private final static Set<Class<?>> SUPPORTED_TYPES = new HashSet<>(
            Arrays.asList(String.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class,
                    Boolean.class,
                    Date.class,
                    LocalDate.class));

    private SingleValuedParameterFactory() {
    }

    /**
     * Creates a new single-valued parameter with the given id and for the specified domain type.
     *
     * @param id      identifier for the parameter
     * @param forType domain type of the parameter value
     * @return an instance of a subclass of {@link AbstractSingleUriParameter} for the given domain type
     * @throws IllegalArgumentException if the specified domain type is not supported by this factory
     */
    public static AbstractSingleUriParameter<?> createUriParameter(final String id, final Class forType) {
        LOG.debug("createUriParameter(): Creating URI parameter with id '{}' for data type {}", id, forType);

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
        if (forType == LocalDate.class) {
            return new SingleLocalDateUriParameter(id);
        }

        return null;
    }
}
