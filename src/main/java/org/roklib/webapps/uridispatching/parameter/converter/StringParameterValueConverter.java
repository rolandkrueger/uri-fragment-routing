package org.roklib.webapps.uridispatching.parameter.converter;

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
