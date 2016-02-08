package org.roklib.webapps.uridispatching.parameter.converter;

/**
 * @author Roland Krüger
 */
public class LongParameterValueConverter implements ParameterValueConverter<Long> {

    public final static LongParameterValueConverter INSTANCE = new LongParameterValueConverter();

    @Override
    public String convertToString(Long value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Long convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return Long.valueOf(valueAsString);
        } catch (NumberFormatException e) {
            throw new ParameterValueConversionException(e);
        }
    }
}
