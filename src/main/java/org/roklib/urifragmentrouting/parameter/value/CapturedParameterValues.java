package org.roklib.urifragmentrouting.parameter.value;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.UriParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation class for {@link CapturedParameterValues}.
 *
 * Contains the set of parameter values which have been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class CapturedParameterValues {

    private Map<String, Map<String, ParameterValue<?>>> values;

    public CapturedParameterValues() {
        values = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <V> ParameterValue<V> getValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return null;
        }

        return (ParameterValue<V>) parameterValues.get(parameterId);
    }

    public <V> void setValueFor(String mapperName, String parameterId, ParameterValue<?> value) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);
        if (value == null) {
            return;
        }

        final Map<String, ParameterValue<?>> mapperValues = values.computeIfAbsent(mapperName, k -> new HashMap<>());
        mapperValues.put(parameterId, value);
    }

    public <V> void setValueFor(String mapperName, UriParameter<V> parameter, ParameterValue<?> value) {
        Preconditions.checkNotNull(parameter);
        setValueFor(mapperName, parameter.getId(), value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> asQueryParameterMap() {
        if (values.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        values.values().stream().forEach(stringParameterValueMap -> {
            stringParameterValueMap.entrySet().stream().forEach(stringParameterValueEntry -> {
                result.put(stringParameterValueEntry.getKey(), stringParameterValueEntry.getValue().getValue().toString());
            });
        });
        return result;
    }

    public <V> ParameterValue<V> removeValueFor(String mapperName, String parameterId) {
        final ParameterValue<V> value = getValueFor(mapperName, parameterId);
        if (value != null) {
            final Map<String, ParameterValue<?>> mapperParameters = values.get(mapperName);
            mapperParameters.remove(parameterId);
            if (mapperParameters.isEmpty()) {
                values.remove(mapperName);
            }
        }
        return value;
    }

    public boolean hasValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return false;
        }
        ParameterValue<?> parameterValue = parameterValues.get(parameterId);
        return parameterValue != null && parameterValue.hasValue();
    }
}
