package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

/**
 * Parameter value converter that converts to Boolean. Valid String representations of <code>true</code> are "1" and
 * "true" whereas valid representations of false are "0" and "false". If the input does not represent one these values a
 * {@link ParameterValueConversionException} is thrown.
 * <p>
 * No new objects of this converter can be created, as there is a static singleton {@link #INSTANCE} of this converter
 * to be used by client code.
 */
public class BooleanParameterValueConverter implements ParameterValueConverter<Boolean> {

    /**
     * Singleton instance of this converter to be used.
     */
    public final static BooleanParameterValueConverter INSTANCE = new BooleanParameterValueConverter();

    private BooleanParameterValueConverter() {
    }

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
