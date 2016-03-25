package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * @author Roland Krüger
 */
public class BooleanParameterValueConverter implements ParameterValueConverter<Boolean> {

    public final static BooleanParameterValueConverter INSTANCE = new BooleanParameterValueConverter();

    @Override
    public String convertToString(Boolean value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    @Override
    public Boolean convertToValue(String value) throws ParameterValueConversionException {
        if (!(value.equals("1") || value.equals("0") || value.equals("false") || value
                .equals("true"))) {
            throw new ParameterValueConversionException();
        }

        if (value.equals("1")) {
            return Boolean.TRUE;
        }

        if (value.equals("0")) {
            return Boolean.FALSE;
        }

        return Boolean.valueOf(value);
    }
}
