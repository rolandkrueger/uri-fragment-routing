package org.roklib.urifragmentrouting.parameter.converter;

public class FloatParameterValueConverterTest extends AbstractParameterValueConverterTest<Float> {
    @Override
    protected ParameterValueConverter<Float> getConverter() {
        return FloatParameterValueConverter.INSTANCE;
    }

    @Override
    protected Float getExpectedValue() {
        return 12.34f;
    }

    @Override
    protected String getStringForExpectedValue() {
        return "12.34";
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }
}
