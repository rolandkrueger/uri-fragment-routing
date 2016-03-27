package org.roklib.urifragmentrouting.parameter.converter;

import org.junit.Test;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ISO8601ToLocalDateParameterValueConverterTest {

    @Test
    public void testConversion() throws ParameterValueConversionException {
        ISO8601ToLocalDateParameterValueConverter converter = ISO8601ToLocalDateParameterValueConverter.INSTANCE;
        LocalDate now = LocalDate.now();
        final String convertedDateString = converter.convertToString(now);
        final LocalDate convertedDate = converter.convertToValue(convertedDateString);
        assertThat(convertedDate, is(now));
    }

    @Test(expected = ParameterValueConversionException.class)
    public void testConversionWithException() throws ParameterValueConversionException {
        ISO8601ToLocalDateParameterValueConverter converter = ISO8601ToLocalDateParameterValueConverter.INSTANCE;
        converter.convertToValue("no date");
    }
}