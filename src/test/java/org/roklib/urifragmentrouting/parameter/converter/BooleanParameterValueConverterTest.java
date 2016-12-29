package org.roklib.urifragmentrouting.parameter.converter;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanParameterValueConverterTest extends AbstractParameterValueConverterTest<Boolean> {
    @Override
    protected ParameterValueConverter<Boolean> getConverter() {
        return BooleanParameterValueConverter.INSTANCE;
    }

    @Override
    protected Boolean getExpectedValue() {
        return Boolean.TRUE;
    }

    @Override
    protected String getStringForExpectedValue() {
        return "true";
    }

    @Override
    protected String getInvalidStringValue() {
        return "Y";
    }

    @Test
    public void testOtherBooleanValues() throws Exception {
        assertThat(getConverter().convertToValue("1"), is(Boolean.TRUE));
        assertThat(getConverter().convertToValue("0"), is(Boolean.FALSE));
        assertThat(getConverter().convertToValue("false"), is(Boolean.FALSE));
    }
}
