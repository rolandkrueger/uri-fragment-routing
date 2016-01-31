package org.roklib.webapps.uridispatching.parameter.value;

import java.util.Optional;

/**
 * @author rkrueger
 */
public interface CapturedParameterValues {
    @SuppressWarnings("unchecked")
    <V> Optional<ParameterValue<V>> getValueFor(String mapperName, String parameterId);

    boolean isEmpty();

    <V> boolean hasValueFor(String mapperName, String parameterId);
}
