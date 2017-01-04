package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.ISO8601ToLocalDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.time.LocalDate;

/**
 * Single-valued URI parameter with domain type {@link LocalDate}. By specifying an own {@link ParameterValueConverter}
 * with {@link #SingleLocalDateUriParameter(String, ParameterValueConverter)}, the method how to convert a String into a
 * {@link LocalDate} can be customized.
 */
public class SingleLocalDateUriParameter extends AbstractSingleUriParameter<LocalDate> {

    /**
     * Constructs a new LocalDate-typed URI parameter with the specified parameter name. The converter used is by
     * default {@link ISO8601ToLocalDateParameterValueConverter#INSTANCE}.
     *
     * @param parameterName parameter name to be used for this URI parameter
     */
    public SingleLocalDateUriParameter(final String parameterName) {
        this(parameterName, ISO8601ToLocalDateParameterValueConverter.INSTANCE);
    }

    /**
     * Constructs a new LocalDate-typed URI parameter with the specified parameter name.
     *
     * @param parameterName parameter name to be used for this URI parameter
     * @param converter parameter value converter to be used
     */
    public SingleLocalDateUriParameter(final String parameterName, final ParameterValueConverter<LocalDate> converter) {
        super(parameterName, converter);
    }
}
