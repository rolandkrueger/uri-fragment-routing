package org.roklib.urifragmentrouting.parameter.converter;

import org.junit.Test;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringListParameterValueConverterTest {

    @Test
    public void testConversion() throws ParameterValueConversionException {
        final List<String> stringList = Arrays.asList("foo", "bar/baz", "foo;bar");
        StringListParameterValueConverter converter = StringListParameterValueConverter.INSTANCE;
        final String convertedString = converter.convertToString(stringList);
        assertThat(convertedString, is("foo;bar%2Fbaz;foo%3Bbar"));

        final List<String> convertedList = converter.convertToValue(convertedString);
        assertThat(convertedList, is(stringList));
    }
}