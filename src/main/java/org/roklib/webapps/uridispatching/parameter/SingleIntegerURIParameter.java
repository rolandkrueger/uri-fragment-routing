package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleIntegerUriParameter extends AbstractSingleUriParameter<Integer> {
    private static final long serialVersionUID = - 8886216456838021135L;

    public SingleIntegerUriParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    public ParameterValue<Integer> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Integer.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
        }
    }

}
