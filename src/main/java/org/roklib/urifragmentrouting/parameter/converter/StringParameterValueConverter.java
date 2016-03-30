package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to String which means that it doesn't do anything with the input. The input
 * is simply returned unaltered from the conversion methods.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class StringParameterValueConverter implements ParameterValueConverter<String> {
    /**
     * Singleton instance of this converter to be used.
     */
    public final static StringParameterValueConverter INSTANCE = new StringParameterValueConverter();

    private StringParameterValueConverter() {
    }

    @Override
    public String convertToString(String value) {
        return value;
    }

    @Override
    public String convertToValue(String valueAsString) throws ParameterValueConversionException {
        return valueAsString;
    }
}
