package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.IntegerParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

public class SingleIntegerUriParameterTest extends AbstractSingleUriParameterTest<Integer> {
    @Override
    public AbstractSingleUriParameter<Integer> getTestSingleURIParameter(String parameterName) {
        SingleIntegerUriParameter result = new SingleIntegerUriParameter("test");
        return result;
    }

    @Override
    public ParameterValueConverter<Integer> getTypeConverter() {
        return IntegerParameterValueConverter.INSTANCE;
    }

    @Override
    public Integer getTestValue() {
        return 123;
    }

    @Override
    public Integer getDefaultValue() {
        return 42;
    }
}
