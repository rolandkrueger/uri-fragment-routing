package org.roklib.urifragmentrouting.parameter;

import org.junit.Test;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.converter.AbstractParameterValueConverterTest;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class IdWithTextParameterValueConverterTest extends AbstractParameterValueConverterTest<SingleLongWithIgnoredTextUriParameter.IdWithText> {
    @Override
    protected ParameterValueConverter<SingleLongWithIgnoredTextUriParameter.IdWithText> getConverter() {
        return SingleLongWithIgnoredTextUriParameter.IdWithTextParameterValueConverter.INSTANCE;
    }

    @Override
    protected SingleLongWithIgnoredTextUriParameter.IdWithText getExpectedValue() {
        return new SingleLongWithIgnoredTextUriParameter.IdWithTextImpl(123L, "_text");
    }

    @Override
    protected String getStringForExpectedValue() {
        return "123_text";
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }

    @Test
    public void testConvertToString_id_is_null() throws Exception {
        final SingleLongWithIgnoredTextUriParameter.IdWithText idWithText = new SingleLongWithIgnoredTextUriParameter.IdWithTextImpl();
        final String convertedToString = getConverter().convertToString(idWithText);
        assertThat(convertedToString, is(""));
    }

    @Test(expected = ParameterValueConversionException.class)
    public void testConvertToValue_with_invalid_number() throws Exception {
        getConverter().convertToValue("99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999_text");
    }
}
