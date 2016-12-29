package org.roklib.urifragmentrouting.parameter.converter;

public class IntegerParameterValueConverterTest extends AbstractParameterValueConverterTest<Integer> {

    @Override
    protected ParameterValueConverter<Integer> getConverter() {
        return IntegerParameterValueConverter.INSTANCE;
    }

    @Override
    protected Integer getExpectedValue() {
        return 10_000_000;
    }

    @Override
    protected String getStringForExpectedValue() {
        return "10000000";
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }
}
