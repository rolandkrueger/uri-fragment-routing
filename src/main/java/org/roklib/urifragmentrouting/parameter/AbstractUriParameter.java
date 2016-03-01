package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.Map;

public abstract class AbstractUriParameter<V> implements UriParameter<V> {
    private static final long serialVersionUID = 2304452724109724238L;

    private V defaultValue;
    private boolean optional;
    private final String id;
    private ParameterValueConverter<V> converter;

    protected AbstractUriParameter(String id, ParameterValueConverter<V> converter) {
        this(id);
        this.converter = converter;
    }

    public AbstractUriParameter(String id) {
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

    public ParameterValueConverter<V> getConverter() {
        return converter;
    }

    public ParameterValue<V> consumeParameters(Map<String, String> parameters){
        final ParameterValue<V> result = consumeParametersImpl(parameters);
        return postConsume(result);
    }

    protected abstract ParameterValue<V> consumeParametersImpl(Map<String, String> parameters);

    private ParameterValue<V> postConsume(ParameterValue<V> value) {
        if (value == null && defaultValue != null && optional) {
            return ParameterValue.forDefaultValue(defaultValue);
        }
        if (value == null) {
            return ParameterValue.forError(UriParameterError.PARAMETER_NOT_FOUND);
        }
        return value;
    }

    public void setOptional(V defaultValue) {
        Preconditions.checkNotNull(defaultValue);
        this.optional = true;
        this.defaultValue = defaultValue;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public void setConverter(ParameterValueConverter<V> converter) {
        this.converter = converter;
    }
}
