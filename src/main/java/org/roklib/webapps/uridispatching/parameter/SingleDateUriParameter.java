package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.Date;

public class SingleDateUriParameter extends AbstractSingleUriParameter<Date> {
    private static final long serialVersionUID = 6617369364956822893L;

    public SingleDateUriParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    protected ParameterValue<Date> consumeParametersImpl(String value) {
        try {
            return ParameterValue.forValue(new Date(Long.valueOf(value)));
        } catch (NumberFormatException nfExc) {
            return ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
        }
    }
}
