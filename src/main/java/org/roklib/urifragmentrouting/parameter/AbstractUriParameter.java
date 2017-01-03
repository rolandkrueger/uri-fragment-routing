package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.Map;

/**
 * Abstract base implementation of interface {@link UriParameter}.
 *
 * @param <V> domain type of the parameter value
 */
public abstract class AbstractUriParameter<V> implements UriParameter<V> {
    private static final long serialVersionUID = 2304452724109724238L;

    private V defaultValue;
    private boolean optional;
    private final String id;
    private ParameterValueConverter<V> converter;

    protected AbstractUriParameter(final String id, final ParameterValueConverter<V> converter) {
        this(id);
        this.converter = converter;
    }

    public AbstractUriParameter(final String id) {
        Preconditions.checkNotNull(id);
        if ("".equals(id.trim())) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.id = id;
        optional = false;
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public V getDefaultValue() {
        return defaultValue;
    }

    @Override
    public ParameterValueConverter<V> getConverter() {
        return converter;
    }

    @Override
    public ParameterValue<V> consumeParameters(final Map<String, String> parameters) {
        final ParameterValue<V> result = consumeParametersImpl(parameters);
        return postConsume(result);
    }

    /**
     * Finds the value belonging to this parameter in the given map and returns a {@link ParameterValue} object
     * configured correspondingly. This method has to be implemented by subclasses. It is invoked each time that a
     * particular URI fragment is interpreted by the {@link org.roklib.urifragmentrouting.UriActionMapperTree}.
     * <p>
     * When a URI fragment is interpreted, all parameter values found in that fragment are collected in a map with the
     * parameter names as keys and the respective parameter values as values. This map is passed into each {@link
     * UriParameter} registered on the URI path segment mappers found in this fragment. A URI parameter is the
     * responsible for picking out the value from this map it is responsible for.
     * <p>
     * Take for example the following URI fragment:
     * <p>
     * <tt>products/id/17/details/mode/summary</tt>
     * <p>
     * It contains the two parameters <tt>id</tt> and <tt>mode</tt>. This URI fragment is transformed into the following
     * parameter value map:
     * <p>
     * <pre>
     * [   "id"   =&gt; "17"
     *     "mode" =&gt; "summary  ]
     * </pre>
     * When this map is passed into the URI parameter object responsible for the <tt>id</tt> parameter, it will read the
     * <tt>id</tt>-value from the map, put it into a {@link ParameterValue} object and return this object.
     * <p>
     * <b>Important:</b> When no value(s) could be found for this parameter, it is mandatory to return null. In
     * particular, no {@link ParameterValue} object should be returned with an error type {@link
     * UriParameterError#PARAMETER_NOT_FOUND}. Such an object might be returned by this abstract class in a later step
     * when there is no default value specified for this parameter.
     *
     * @param parameters the set of parameter name/value assignments found in the currently interpreted URI fragment
     * @return a {@link ParameterValue} object which contains a value for this parameter if the corresponding value
     * could be found in the map or null if no value was found.
     */
    protected abstract ParameterValue<V> consumeParametersImpl(Map<String, String> parameters);

    private ParameterValue<V> postConsume(final ParameterValue<V> value) {
        if (value == null && defaultValue != null && optional) {
            return ParameterValue.forDefaultValue(defaultValue);
        }
        if (value == null) {
            return ParameterValue.forError(UriParameterError.PARAMETER_NOT_FOUND);
        }
        return value;
    }

    @Override
    public void setOptional(final V defaultValue) {
        Preconditions.checkNotNull(defaultValue);
        this.optional = true;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setConverter(final ParameterValueConverter<V> converter) {
        this.converter = converter;
    }
}
