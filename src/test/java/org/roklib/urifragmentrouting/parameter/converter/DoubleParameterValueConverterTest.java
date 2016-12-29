package org.roklib.urifragmentrouting.parameter.converter;

public class DoubleParameterValueConverterTest extends AbstractParameterValueConverterTest<Double> {
    @Override
    protected ParameterValueConverter<Double> getConverter() {
        return DoubleParameterValueConverter.INSTANCE;
    }

    @Override
    protected Double getExpectedValue() {
        return 9.18E+09d;
    }

    @Override
    protected String getStringForExpectedValue() {
        return "9.18E9";
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }
}
