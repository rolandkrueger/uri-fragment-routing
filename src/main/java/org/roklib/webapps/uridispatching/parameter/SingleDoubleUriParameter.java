package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.parameter.converter.DoubleParameterValueConverter;

public class SingleDoubleUriParameter extends AbstractSingleUriParameter<Double> {
    private static final long serialVersionUID = - 8782412809369726453L;

    public SingleDoubleUriParameter(String parameterName) {
        super(parameterName, DoubleParameterValueConverter.INSTANCE);
    }
}
