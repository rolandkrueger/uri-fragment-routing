package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

public class SingleLongURIParameter extends AbstractSingleURIParameter<Long> {
    private static final long serialVersionUID = -5213198758703615905L;

    public SingleLongURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    protected ParameterValue<Long> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(Long.valueOf(value));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }
    }

}
