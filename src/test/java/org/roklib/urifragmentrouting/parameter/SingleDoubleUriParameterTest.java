package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.DoubleParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

public class SingleDoubleUriParameterTest extends AbstractSingleUriParameterTest<Double> {
    @Override
    public AbstractSingleUriParameter<Double> getTestSingleURIParameter(String parameterName) {
        return new SingleDoubleUriParameter("test");
    }

    @Override
    public ParameterValueConverter<Double> getTypeConverter() {
        return DoubleParameterValueConverter.INSTANCE;
    }

    @Override
    public Double getTestValue() {
        return 17.0d;
    }

    @Override
    public Double getDefaultValue() {
        return 23.0d;
    }
}
