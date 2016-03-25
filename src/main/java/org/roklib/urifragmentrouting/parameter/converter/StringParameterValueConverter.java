package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * @author Roland Krüger
 */
public class StringParameterValueConverter implements ParameterValueConverter<String> {

    public final static StringParameterValueConverter INSTANCE = new StringParameterValueConverter();

    @Override
    public String convertToString(String value) {
        return value;
    }

    @Override
    public String convertToValue(String valueAsString) throws ParameterValueConversionException {
        return valueAsString;
    }
}
