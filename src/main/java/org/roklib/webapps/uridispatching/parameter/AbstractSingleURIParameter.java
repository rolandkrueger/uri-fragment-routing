package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractSingleUriParameter<V extends Serializable> extends AbstractUriParameter<V> {
    private static final long serialVersionUID = -4048110873045678896L;

    public AbstractSingleUriParameter(String parameterName) {
        super(parameterName);
    }

    public int getSingleValueCount() {
        return 1;
    }

    public List<String> getParameterNames() {
        return Collections.singletonList(getId());
    }

    public void parameterizeURIHandler(AbstractUriPathSegmentActionMapper handler) {
//        if (value != null) {
//            handler.addActionArgument(parameterName.get(0), value);
//        }
    }

    protected final ParameterValue<V> consumeParametersImpl(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.get(getId());
        if (!(valueList == null || valueList.isEmpty())) {
            return consumeParametersImpl(valueList.get(0));
        }
        return null;
    }

    protected abstract ParameterValue<V> consumeParametersImpl(String value);

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + ": " + getId() + "}";
    }
}
