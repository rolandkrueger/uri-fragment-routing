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

    public SingleLocalDateUriParameter(String parameterName) {
        this(parameterName, ISO8601ToLocalDateParameterValueConverter.INSTANCE);
    }

    public SingleLocalDateUriParameter(String parameterName, ParameterValueConverter<LocalDate> converter) {
        super(parameterName, converter);
    }
}
