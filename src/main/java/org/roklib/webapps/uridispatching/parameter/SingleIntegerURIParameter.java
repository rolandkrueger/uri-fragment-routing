package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleIntegerURIParameter extends AbstractSingleURIParameter<Integer> {
    private static final long serialVersionUID = - 8886216456838021135L;

    public SingleIntegerURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    public ParameterValue<Integer> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Integer.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }
    }

}
