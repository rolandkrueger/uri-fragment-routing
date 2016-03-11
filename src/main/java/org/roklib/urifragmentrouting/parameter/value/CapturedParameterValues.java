package org.roklib.urifragmentrouting.parameter.value;

import java.util.Map;

/**
 * @author Roland Krüger
 */
public interface CapturedParameterValues {

    @SuppressWarnings("unchecked")
    <V> ParameterValue<V> getValueFor(String mapperName, String parameterId);

    <V> void setValueFor(String mapperName, String parameterId, ParameterValue<?> value);

    boolean isEmpty();

    <V> boolean hasValueFor(String mapperName, String parameterId);

    Map<String, String> asQueryParameterMap();

    <V> ParameterValue<V> removeValueFor(String mapperName, String parameterId);
}
