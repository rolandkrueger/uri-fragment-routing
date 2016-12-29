package org.roklib.urifragmentrouting.parameter.converter;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public abstract class AbstractParameterValueConverterTest<T> {

    private ParameterValueConverter<T> converter;

    protected abstract ParameterValueConverter<T> getConverter();

    protected abstract T getExpectedValue();

    protected abstract String getStringForExpectedValue();

    protected abstract String getInvalidStringValue();

    @Before
    public void setUp() throws Exception {
        converter = getConverter();
    }

    @Test
    public void testConvertToString() throws Exception {
        final String convertedToString = converter.convertToString(getExpectedValue());
        assertThat(convertedToString, is(getStringForExpectedValue()));
    }

    @Test
    public void testConvertToValue_successful() throws Exception {
        final T value = converter.convertToValue(getStringForExpectedValue());
        assertThat(value, is(getExpectedValue()));
    }

    @Test(expected = ParameterValueConversionException.class)
    public void testConvertToValue_failure() throws Exception {
        converter.convertToValue(getInvalidStringValue());
    }

    @Test
    public void testConvertNullToString() throws Exception {
        final String convertedToString = converter.convertToString(null);
        assertThat(convertedToString, is(""));
    }
}
