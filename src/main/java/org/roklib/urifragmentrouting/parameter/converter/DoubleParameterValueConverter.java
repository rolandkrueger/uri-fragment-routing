package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to Double. When the input String could not be converted into a double value
 * the corresponding {@link NumberFormatException} is wrapped in a {@link ParameterValueConversionException}.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class DoubleParameterValueConverter implements ParameterValueConverter<Double> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static DoubleParameterValueConverter INSTANCE = new DoubleParameterValueConverter();

    private DoubleParameterValueConverter() {
    }

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
