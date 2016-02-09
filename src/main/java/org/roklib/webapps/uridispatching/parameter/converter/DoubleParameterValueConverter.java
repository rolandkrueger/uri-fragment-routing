package org.roklib.webapps.uridispatching.parameter.converter;

/**
 * @author Roland Krüger
 */
public class DoubleParameterValueConverter implements ParameterValueConverter<Double>{

    public final static DoubleParameterValueConverter INSTANCE = new DoubleParameterValueConverter();

    @Override
    public String convertToString(Double value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Double convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return Double.valueOf(valueAsString);
        } catch (NumberFormatException e) {
            throw new ParameterValueConversionException(e);
        }
    }
}