package org.roklib.webapps.uridispatching.parameter.value;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.URIParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contains the set of parameter values which have been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class ConsumedParameterValues {

    private Map<String, Map<URIParameter<?>, ParameterValue<?>>> values;

    public ConsumedParameterValues() {
        values = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <V> Optional<ParameterValue<V>> getValueFor(String mapperName, URIParameter<V> parameter) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);

        final Map<URIParameter<?>, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((ParameterValue<V>) parameterValues.get(parameter));
    }

    public <V> void setValueFor(String mapperName, URIParameter<V> parameter, V value) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);

        final Map<URIParameter<?>, ParameterValue<?>> mapperValues = values.computeIfAbsent(mapperName, k -> new HashMap<>());
        mapperValues.put(parameter, new ParameterValue<>(value));
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public <V> boolean hasValueFor(String mapperName, URIParameter<V> parameter) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);

        final Map<URIParameter<?>, ParameterValue<?>> parameterValues = values.get(mapperName);
        return parameterValues != null && parameterValues.containsKey(parameter);
    }
}
