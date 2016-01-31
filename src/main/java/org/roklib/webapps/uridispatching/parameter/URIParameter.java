package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface URIParameter<V> extends Serializable {

    ParameterValue<V> consumeParameters(Map<String, List<String>> parameters);

    @Deprecated
    void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler);

    String getId();

    void setOptional(V defaultValue);

    boolean isOptional();

    int getSingleValueCount();

    List<String> getParameterNames();
}
