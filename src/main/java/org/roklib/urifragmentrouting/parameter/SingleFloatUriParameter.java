package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.FloatParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Float.
 */
public class SingleFloatUriParameter extends AbstractSingleUriParameter<Float> {
    private static final long serialVersionUID = 998024667059320476L;

    /**
     * Constructs a new Float-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link FloatParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleFloatUriParameter(final String parameterName) {
        super(parameterName, FloatParameterValueConverter.INSTANCE);
    }
}
