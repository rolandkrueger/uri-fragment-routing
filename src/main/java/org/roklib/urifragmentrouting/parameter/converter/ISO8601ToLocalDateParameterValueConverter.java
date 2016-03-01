package org.roklib.urifragmentrouting.parameter.converter;

import java.time.LocalDate;

/**
 * @author Roland Krüger
 */
public class ISO8601ToLocalDateParameterValueConverter implements ParameterValueConverter<LocalDate> {

    public final static ISO8601ToLocalDateParameterValueConverter INSTANCE = new ISO8601ToLocalDateParameterValueConverter();

    @Override
    public String convertToString(LocalDate value) {
        return value.toString();
    }

    @Override
    public LocalDate convertToValue(String valueAsString) throws ParameterValueConversionException {
        return LocalDate.parse(valueAsString);
    }
}
