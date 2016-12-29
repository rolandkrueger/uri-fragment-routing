package org.roklib.urifragmentrouting.parameter.converter;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class StringParameterValueConverterTest {

    private final StringParameterValueConverter converter = StringParameterValueConverter.INSTANCE;

    @Test
    public void testConvertToString() throws Exception {
        assertThat(converter.convertToString("string"), is("string"));
    }

    @Test
    public void testConvertToValue() throws Exception {
        assertThat(converter.convertToValue("string"), is("string"));
    }

    @Test
    public void testConvertToString_with_null() throws Exception {
        assertThat(converter.convertToString(null), is(nullValue()));
    }

    @Test
    public void testConvertToValue_with_null() throws Exception {
        assertThat(converter.convertToValue(null), is(nullValue()));
    }
}
