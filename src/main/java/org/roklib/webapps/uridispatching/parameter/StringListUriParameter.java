package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
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
    public void parameterizeURIHandler(AbstractUriPathSegmentActionMapper handler) {
    }

    @Override
    public int getSingleValueCount() {
        return 1;
    }

    @Override
    public List<String> getParameterNames() {
        return Collections.singletonList(getId());
    }
}
