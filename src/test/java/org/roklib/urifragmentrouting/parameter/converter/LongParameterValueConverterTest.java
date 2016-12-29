package org.roklib.urifragmentrouting.parameter.converter;

public class LongParameterValueConverterTest extends AbstractParameterValueConverterTest<Long> {
    @Override
    protected ParameterValueConverter<Long> getConverter() {
        return LongParameterValueConverter.INSTANCE;
    }

    @Override
    protected Long getExpectedValue() {
        return 123L;
    }

    @Override
    protected String getStringForExpectedValue() {
        return "123";
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }
}
