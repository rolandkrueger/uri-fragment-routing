package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to Float. When the input String could not be converted into a float value the
 * corresponding {@link NumberFormatException} is wrapped in a {@link ParameterValueConversionException}.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class FloatParameterValueConverter implements ParameterValueConverter<Float> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static FloatParameterValueConverter INSTANCE = new FloatParameterValueConverter();

    private FloatParameterValueConverter() {
    }

    @Override
    public String convertToString(final Float value) {
        if (value == null) {
            return "";
        }

        return value.toString();
    }

    @Override
    public Float convertToValue(final String valueAsString) throws ParameterValueConversionException {
        try {
            return Float.valueOf(valueAsString);
        } catch (final NumberFormatException e) {
            throw new ParameterValueConversionException(valueAsString + " could not be converted into an object of type Float", e);
        }
    }
}
