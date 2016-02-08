package org.roklib.webapps.uridispatching.parameter.converter;

import java.util.Date;

/**
 * @author Roland Krüger
 */
public class LongToDateParameterValueConverter implements ParameterValueConverter<Date> {

    public final static LongToDateParameterValueConverter INSTANCE = new LongToDateParameterValueConverter();

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
