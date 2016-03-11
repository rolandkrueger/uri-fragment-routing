package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.FloatParameterValueConverter;

public class SingleFloatUriParameter extends AbstractSingleUriParameter<Float> {
    private static final long serialVersionUID = 998024667059320476L;

    public SingleFloatUriParameter(String parameterName) {
        super(parameterName, FloatParameterValueConverter.INSTANCE);
    }
}