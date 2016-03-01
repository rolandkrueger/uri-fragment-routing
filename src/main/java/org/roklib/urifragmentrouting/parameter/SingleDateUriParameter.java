package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.LongToDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.Date;

public class SingleDateUriParameter extends AbstractSingleUriParameter<Date> {
    private static final long serialVersionUID = 6617369364956822893L;

    public SingleDateUriParameter(String parameterName, ParameterValueConverter<Date> converter) {
        super(parameterName, converter);
    }

    public SingleDateUriParameter(String parameterName) {
        this(parameterName, LongToDateParameterValueConverter.INSTANCE);
    }
}
