package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * @author Roland Krüger
 */
public class ISO8601ToLocalDateParameterValueConverter implements ParameterValueConverter<LocalDate> {

    public final static ISO8601ToLocalDateParameterValueConverter INSTANCE = new ISO8601ToLocalDateParameterValueConverter();

    private ISO8601ToLocalDateParameterValueConverter() {
    }

    @Override
    public String convertToString(LocalDate value) {
        return value.toString();
    }

    @Override
    public LocalDate convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return LocalDate.parse(valueAsString);
        } catch (DateTimeParseException dtpExc) {
            throw new ParameterValueConversionException(dtpExc);
        }
    }
}
