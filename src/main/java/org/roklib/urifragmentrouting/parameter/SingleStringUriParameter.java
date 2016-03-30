package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.StringParameterValueConverter;

/**
 * Single-valued URI parameter with domain type String.
 */
public class SingleStringUriParameter extends AbstractSingleUriParameter<String> {
    private static final long serialVersionUID = -9010093565464929620L;

    public SingleStringUriParameter(String parameterName) {
        super(parameterName, StringParameterValueConverter.INSTANCE);
    }
}
