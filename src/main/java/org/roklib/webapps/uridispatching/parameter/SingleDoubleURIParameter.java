package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleDoubleUriParameter extends AbstractSingleUriParameter<Double> {
    private static final long serialVersionUID = - 8782412809369726453L;

    public SingleDoubleUriParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    public ParameterValue<Double> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Double.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
        }
    }
}
