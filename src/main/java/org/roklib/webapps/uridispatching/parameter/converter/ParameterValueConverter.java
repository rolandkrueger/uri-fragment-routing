package org.roklib.webapps.uridispatching.parameter.converter;

import java.io.Serializable;

/**
 * @author Roland Krüger
 */
public interface ParameterValueConverter<T> extends Serializable {
    String convertToString(T value);

    T convertToValue(String valueAsString) throws ParameterValueConversionException;
}
