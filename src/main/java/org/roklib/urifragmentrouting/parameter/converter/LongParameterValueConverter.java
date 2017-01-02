package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to Long. When the input String could not be converted into a Long value the
 * corresponding {@link NumberFormatException} is wrapped in a {@link ParameterValueConversionException}.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class LongParameterValueConverter implements ParameterValueConverter<Long> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static LongParameterValueConverter INSTANCE = new LongParameterValueConverter();

    private LongParameterValueConverter() {
    }

    @Override
    public String convertToString(final Long value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Long convertToValue(final String valueAsString) throws ParameterValueConversionException {
        try {
            return Long.valueOf(valueAsString);
        } catch (final NumberFormatException e) {
            throw new ParameterValueConversionException(valueAsString + " could not be converted into an object of type Long", e);
        }
    }
}
