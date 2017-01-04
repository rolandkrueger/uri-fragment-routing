package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.StringParameterValueConverter;

/**
 * Single-valued URI parameter with domain type String.
 */
public class SingleStringUriParameter extends AbstractSingleUriParameter<String> {
    private static final long serialVersionUID = -9010093565464929620L;

    /**
     * Constructs a new Date-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link StringParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleStringUriParameter(final String parameterName) {
        super(parameterName, StringParameterValueConverter.INSTANCE);
    }
}
