package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.converter.LongParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;

public class SingleLongUriParameterTest extends AbstractSingleUriParameterTest<Long> {
    @Override
    public AbstractSingleUriParameter<Long> getTestSingleURIParameter(String parameterName) {
        return new SingleLongUriParameter("test");
    }

    @Override
    public ParameterValueConverter<Long> getTypeConverter() {
        return LongParameterValueConverter.INSTANCE;
    }

    @Override
    public Long getTestValue() {
        return 1234L;
    }

    @Override
    public Long getDefaultValue() {
        return 999L;
    }
}
