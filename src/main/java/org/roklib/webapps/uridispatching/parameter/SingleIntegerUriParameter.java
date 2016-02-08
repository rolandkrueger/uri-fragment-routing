package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.converter.IntegerParameterValueConverter;

public class SingleIntegerUriParameter extends AbstractSingleUriParameter<Integer> {
    private static final long serialVersionUID = - 8886216456838021135L;

    public SingleIntegerUriParameter(String parameterName) {
        super(parameterName, IntegerParameterValueConverter.INSTANCE);
    }
}
