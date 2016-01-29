package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleFloatURIParameter extends AbstractSingleURIParameter<Float> {
    private static final long serialVersionUID = 998024667059320476L;

    public SingleFloatURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    protected ParameterValue<Float> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Float.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }
    }

}
