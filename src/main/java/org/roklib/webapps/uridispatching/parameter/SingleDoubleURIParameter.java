package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleDoubleURIParameter extends AbstractSingleURIParameter<Double> {
    private static final long serialVersionUID = - 8782412809369726453L;

    public SingleDoubleURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    public ParameterValue<Double> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Double.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }
    }
}
