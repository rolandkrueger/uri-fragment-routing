package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.DoubleParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Double.
 */
public class SingleDoubleUriParameter extends AbstractSingleUriParameter<Double> {
    private static final long serialVersionUID = -8782412809369726453L;

    /**
     * Constructs a new Double-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link DoubleParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleDoubleUriParameter(final String parameterName) {
        super(parameterName, DoubleParameterValueConverter.INSTANCE);
    }
}
