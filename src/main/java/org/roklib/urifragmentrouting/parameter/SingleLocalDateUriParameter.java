package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.ISO8601ToLocalDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.time.LocalDate;

/**
 * @author Roland Krüger
 */
public class SingleLocalDateUriParameter extends AbstractSingleUriParameter<LocalDate> {

    public SingleLocalDateUriParameter(String parameterName) {
        this(parameterName, ISO8601ToLocalDateParameterValueConverter.INSTANCE);
    }

    public SingleLocalDateUriParameter(String parameterName, ParameterValueConverter<LocalDate> converter) {
        super(parameterName, converter);
    }
}