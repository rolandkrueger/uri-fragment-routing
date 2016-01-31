package org.roklib.webapps.uridispatching.parameter.value;

/**
 * @author rkrueger
 */
public interface CapturedParameterValues {
    @SuppressWarnings("unchecked")
    <V> ParameterValue<V> getValueFor(String mapperName, String parameterId);

    boolean isEmpty();

    <V> boolean hasValueFor(String mapperName, String parameterId);
}
