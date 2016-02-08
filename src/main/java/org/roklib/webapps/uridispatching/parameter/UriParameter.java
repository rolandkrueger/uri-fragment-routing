package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface UriParameter<V> extends Serializable {

    ParameterValue<V> consumeParameters(Map<String, String> parameters);

    String getId();

    void setOptional(V defaultValue);

    boolean isOptional();

    int getSingleValueCount();

    List<String> getParameterNames();

    void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, UriPathSegmentActionMapper.ParameterMode parameterMode);
}
