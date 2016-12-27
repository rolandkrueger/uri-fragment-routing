package org.roklib.urifragmentrouting.parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

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

    private final static HashMap<Class<?>, Function<String, AbstractSingleUriParameter<?>>> SUPPORTED_TYPES_SUPPLIERS;

    static {
        SUPPORTED_TYPES_SUPPLIERS = new HashMap<>();
        SUPPORTED_TYPES_SUPPLIERS.put(String.class, SingleStringUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Integer.class, SingleIntegerUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Long.class, SingleLongUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Float.class, SingleFloatUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Double.class, SingleDoubleUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Boolean.class, SingleBooleanUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(Date.class, SingleDateUriParameter::new);
        SUPPORTED_TYPES_SUPPLIERS.put(LocalDate.class, SingleLocalDateUriParameter::new);
    }

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

        if (SUPPORTED_TYPES_SUPPLIERS.containsKey(forType)) {
            return SUPPORTED_TYPES_SUPPLIERS.get(forType).apply(id);
        }

        throw new IllegalArgumentException("Class " + forType + " is not supported as single valued URI parameter. " +
                "Use one of the following classes: " + SUPPORTED_TYPES_SUPPLIERS.keySet());
    }
}
