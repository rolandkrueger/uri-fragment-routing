package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.LongParameterValueConverter;

/**
 * Single-valued URI parameter with domain type Long.
 */
public class SingleLongUriParameter extends AbstractSingleUriParameter<Long> {
    private static final long serialVersionUID = -5213198758703615905L;

    /**
     *Constructs a new LocalDate-typed URI parameter with the specified parameter name. The converter used is by
     * default {@link LongParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleLongUriParameter(final String parameterName) {
        super(parameterName, LongParameterValueConverter.INSTANCE);
    }
}
