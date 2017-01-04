package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.LongToDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.Date;

/**
 * Single-valued URI parameter with domain type {@link Date}. By specifying an own {@link ParameterValueConverter} with
 * {@link #SingleDateUriParameter(String, ParameterValueConverter)}, the method how to convert a String into a {@link
 * Date} can be customized.
 */
public class SingleDateUriParameter extends AbstractSingleUriParameter<Date> {
    private static final long serialVersionUID = 6617369364956822893L;

    /**
     * Constructs a new Date-typed URI parameter with the specified parameter name. 
     *
     * @param parameterName parameter name to be used for this URI parameter
     * @param converter parameter value converter to be used
     */
    public SingleDateUriParameter(final String parameterName, final ParameterValueConverter<Date> converter) {
        super(parameterName, converter);
    }

    /**
     * Constructs a new Date-typed URI parameter with the specified parameter name. The converter used is by default
     * {@link LongToDateParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleDateUriParameter(final String parameterName) {
        this(parameterName, LongToDateParameterValueConverter.INSTANCE);
    }
}
