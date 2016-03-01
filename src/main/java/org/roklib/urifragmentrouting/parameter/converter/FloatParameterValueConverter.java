package org.roklib.urifragmentrouting.parameter.converter;

/**
 * @author Roland Krüger
 */
public class FloatParameterValueConverter implements ParameterValueConverter<Float>{

    public final static FloatParameterValueConverter INSTANCE = new FloatParameterValueConverter();

    @Override
    public String convertToString(Float value) {
        if (value == null){
            return "";
        }

        return value.toString();
    }

    @Override
    public Float convertToValue(String valueAsString) throws ParameterValueConversionException {
        try {
            return Float.valueOf(valueAsString);
        } catch (NumberFormatException e) {
            throw new ParameterValueConversionException(e);
        }
    }
}
