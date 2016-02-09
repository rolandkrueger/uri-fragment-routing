package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.converter.IntegerParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;

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
