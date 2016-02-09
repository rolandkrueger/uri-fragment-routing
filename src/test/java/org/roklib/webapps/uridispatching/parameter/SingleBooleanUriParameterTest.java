package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.converter.BooleanParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;

public class SingleBooleanUriParameterTest extends AbstractSingleUriParameterTest<Boolean> {
    @Override
    public AbstractSingleUriParameter<Boolean> getTestSingleURIParameter(String parameterName) {
        return new SingleBooleanUriParameter("test");
    }

    @Override
    public ParameterValueConverter<Boolean> getTypeConverter() {
        return BooleanParameterValueConverter.INSTANCE;
    }

    @Override
    public Boolean getTestValue() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean getDefaultValue() {
        return Boolean.FALSE;
    }

}
