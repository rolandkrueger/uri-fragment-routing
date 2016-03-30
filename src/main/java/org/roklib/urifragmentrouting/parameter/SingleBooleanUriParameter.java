package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.BooleanParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Boolean.
 */
public class SingleBooleanUriParameter extends AbstractSingleUriParameter<Boolean> {
    private static final long serialVersionUID = 1181515935142386380L;

    public SingleBooleanUriParameter(String parameterName) {
        super(parameterName, BooleanParameterValueConverter.INSTANCE);
    }
}
