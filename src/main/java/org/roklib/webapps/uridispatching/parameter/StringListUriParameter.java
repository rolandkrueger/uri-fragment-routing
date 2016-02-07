package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A URI parameter that takes a list of Strings as its value.
 *
 * @author Roland Krüger
 */
public class StringListUriParameter extends AbstractUriParameter<List<String>> {

    public StringListUriParameter(String id) {
        super(id);
    }

    @Override
    protected ParameterValue<List<String>> consumeParametersImpl(Map<String, List<String>> parameters) {
        if (parameters.containsKey(getId())) {
            return ParameterValue.forValue(new ArrayList<>(parameters.get(getId())));
        } else {
            return null;
        }
    }

    @Override
    public int getSingleValueCount() {
        return 1;
    }

    @Override
    public List<String> getParameterNames() {
        return Collections.singletonList(getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, UriPathSegmentActionMapper.ParameterMode parameterMode) {
        ParameterValue<List<String>> stringListValue = (ParameterValue<List<String>>) value;
        if (value.hasValue()) {
            stringListValue.getValue().stream().forEach(s -> {
                if (parameterMode == UriPathSegmentActionMapper.ParameterMode.DIRECTORY_WITH_NAMES) {
                    uriTokens.add(getId());
                }
                uriTokens.add(s);
            });
        }
    }
}
