package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.FloatParameterValueConverter;

public class SingleFloatUriParameterTest extends AbstractSingleUriParameterTest<Float> {
    @Override
    public AbstractSingleUriParameter<Float> getTestSingleURIParameter(String parameterName) {
        return new SingleFloatUriParameter("test");
    }

    @Override
    public ParameterValueConverter<Float> getTypeConverter() {
        return FloatParameterValueConverter.INSTANCE;
    }

    @Override
    public Float getTestValue() {
        return 23.0f;
    }

    @Override
    public Float getDefaultValue() {
        return 17.0f;
    }
}
