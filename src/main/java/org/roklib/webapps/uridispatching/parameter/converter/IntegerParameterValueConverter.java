package org.roklib.webapps.uridispatching.parameter.converter;

/**
 * @author Roland Krüger
 */
public class IntegerParameterValueConverter implements ParameterValueConverter<Integer> {

    public final static IntegerParameterValueConverter INSTANCE = new IntegerParameterValueConverter();

    @Override
    public String convertToString(Integer value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Integer convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return Integer.valueOf(valueAsString);
        } catch (NumberFormatException e) {
            throw new ParameterValueConversionException(e);
        }
    }
}
