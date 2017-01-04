package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.IntegerParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Integer.
 */
public class SingleIntegerUriParameter extends AbstractSingleUriParameter<Integer> {
    private static final long serialVersionUID = -8886216456838021135L;

    /**
     * Constructs a new Integer-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link IntegerParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleIntegerUriParameter(final String parameterName) {
        super(parameterName, IntegerParameterValueConverter.INSTANCE);
    }
}
