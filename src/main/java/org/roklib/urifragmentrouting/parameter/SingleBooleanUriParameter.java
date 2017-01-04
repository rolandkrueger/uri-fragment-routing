package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.BooleanParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Boolean.
 */
public class SingleBooleanUriParameter extends AbstractSingleUriParameter<Boolean> {
    private static final long serialVersionUID = 1181515935142386380L;

    /**
     * Constructs a new Boolean-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link BooleanParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleBooleanUriParameter(final String parameterName) {
        super(parameterName, BooleanParameterValueConverter.INSTANCE);
    }
}
