package org.roklib.webapps.uridispatching.parameter.converter;

/**
 * @author Roland Krüger
 */
public interface ParameterValueConverter<T> {
    String convertToString(T value);

    T convertToValue(String valueAsString) throws ParameterValueConversionException;
}
