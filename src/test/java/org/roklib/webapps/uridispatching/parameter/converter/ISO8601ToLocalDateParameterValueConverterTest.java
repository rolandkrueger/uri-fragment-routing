package org.roklib.webapps.uridispatching.parameter.converter;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class ISO8601ToLocalDateParameterValueConverterTest {

    @Test
    public void testConversion() throws ParameterValueConversionException {
        ISO8601ToLocalDateParameterValueConverter converter = new ISO8601ToLocalDateParameterValueConverter();
        LocalDate now = LocalDate.now();
        final String convertedDateString = converter.convertToString(now);
        final LocalDate convertedDate = converter.convertToValue(convertedDateString);
        assertThat(convertedDate, is(now));
    }
}