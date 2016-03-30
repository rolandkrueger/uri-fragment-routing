package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.LongParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Long.
 */
public class SingleLongUriParameter extends AbstractSingleUriParameter<Long> {
    private static final long serialVersionUID = -5213198758703615905L;

    public SingleLongUriParameter(String parameterName) {
        super(parameterName, LongParameterValueConverter.INSTANCE);
    }
}
