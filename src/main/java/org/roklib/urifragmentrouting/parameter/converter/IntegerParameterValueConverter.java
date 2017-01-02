package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to Integer. When the input String could not be converted into an integer
 * value the corresponding {@link NumberFormatException} is wrapped in a {@link ParameterValueConversionException}.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class IntegerParameterValueConverter implements ParameterValueConverter<Integer> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static IntegerParameterValueConverter INSTANCE = new IntegerParameterValueConverter();

    private IntegerParameterValueConverter() {
    }

    @Override
    public String convertToString(final Integer value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Integer convertToValue(final String valueAsString) throws ParameterValueConversionException {
        try {
            return Integer.valueOf(valueAsString);
        } catch (final NumberFormatException e) {
            throw new ParameterValueConversionException(valueAsString + " could not be converted into an object of type Integer", e);
        }
    }
}
