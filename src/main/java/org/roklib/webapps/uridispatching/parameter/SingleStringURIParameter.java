package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleStringURIParameter extends AbstractSingleURIParameter<String> {
    private static final long serialVersionUID = -9010093565464929620L;

    public SingleStringURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    public ParameterValue<String> consumeParametersImpl(String value) {
        return ParameterValue.forValue(value);
    }
}
