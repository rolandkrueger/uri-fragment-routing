package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.converter.BooleanParameterValueConverter;

public class SingleBooleanUriParameter extends AbstractSingleUriParameter<Boolean> {
    private static final long serialVersionUID = 1181515935142386380L;

    public SingleBooleanUriParameter(String parameterName) {
        super(parameterName, BooleanParameterValueConverter.INSTANCE);
    }
}
