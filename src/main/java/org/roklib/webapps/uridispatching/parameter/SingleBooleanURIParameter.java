package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleBooleanURIParameter extends AbstractSingleURIParameter<Boolean> {
    private static final long serialVersionUID = 1181515935142386380L;

    public SingleBooleanURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    protected ParameterValue<Boolean> consumeParametersImpl(String value) {
        if (!(value.equals("1") || value.equals("0") || value.equals("false") || value
                .equals("true"))) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }

        if (value.equals("1")) {
            return ParameterValue.forValue(true);
        }

        if (value.equals("0")) {
            return ParameterValue.forValue(false);
        }

        return ParameterValue.forValue(Boolean.valueOf(value));
    }
}
