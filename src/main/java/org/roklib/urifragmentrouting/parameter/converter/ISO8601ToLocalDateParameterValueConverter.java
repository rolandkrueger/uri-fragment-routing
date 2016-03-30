package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * This converter converts a String into a {@link LocalDate} according to a ISO 8601 date format. In order to convert a
 * String into a {@link LocalDate}, method {@link LocalDate#parse(CharSequence)} is used. By that, the String is parsed
 * using  {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE}. If the date could not be parsed the corresponding
 * {@link DateTimeParseException} is wrapped in a {@link ParameterValueConversionException}.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class ISO8601ToLocalDateParameterValueConverter implements ParameterValueConverter<LocalDate> {
    /**
     * Singleton instance of this converter to be used.
     */
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
