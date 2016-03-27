package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.util.Date;

/**
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 *
 * @author Roland Krüger
 */
public class LongToDateParameterValueConverter implements ParameterValueConverter<Date> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static LongToDateParameterValueConverter INSTANCE = new LongToDateParameterValueConverter();

    private LongToDateParameterValueConverter() {
    }

    @Override
    public String convertToString(Date value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value.getTime());
    }

    @Override
    public Date convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return new Date(Long.valueOf(valueAsString));
        } catch (NumberFormatException nfExc) {
            throw new ParameterValueConversionException(nfExc);
        }
    }
}
