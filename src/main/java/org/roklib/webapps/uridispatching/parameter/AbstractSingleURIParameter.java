package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSingleURIParameter<V extends Serializable> extends AbstractURIParameter<V> {
    private static final long serialVersionUID = -4048110873045678896L;

    private final List<String> parameterName;

    public AbstractSingleURIParameter(String parameterName) {
        Preconditions.checkNotNull(parameterName);

        this.parameterName = new LinkedList<String>();
        this.parameterName.add(parameterName);
    }

    protected String getParameterName() {
        return parameterName.get(0);
    }

    public int getSingleValueCount() {
        return 1;
    }

    public List<String> getParameterNames() {
        return parameterName;
    }

    public void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler) {
//        if (value != null) {
//            handler.addActionArgument(parameterName.get(0), value);
//        }
    }

    protected final ParameterValue<V> consumeParametersImpl(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.get(getParameterName());
        if (!(valueList == null || valueList.isEmpty())) {
            return consumeParametersImpl(valueList.get(0));
        }
        return null;
    }

    protected abstract ParameterValue<V> consumeParametersImpl(String value);

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + ": " + parameterName.get(0) + "}";
    }
}
