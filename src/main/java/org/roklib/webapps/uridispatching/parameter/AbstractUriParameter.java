package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;

public abstract class AbstractUriParameter<V> implements UriParameter<V> {
    private static final long serialVersionUID = 2304452724109724238L;

    private V defaultValue;
    private boolean optional;
    private final String id;

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

    public ParameterValue<V> consumeParameters(Map<String, List<String>> parameters){
        final ParameterValue<V> result = consumeParametersImpl(parameters);
        return postConsume(result);
    }

    protected abstract ParameterValue<V> consumeParametersImpl(Map<String, List<String>> parameters);

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
}
