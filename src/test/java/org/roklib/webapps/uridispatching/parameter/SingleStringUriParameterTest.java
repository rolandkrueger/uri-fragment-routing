package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.StringParameterValueConverter;

public class SingleStringUriParameterTest extends AbstractSingleUriParameterTest<String> {

    @Override
    public AbstractSingleUriParameter<String> getTestSingleURIParameter(String parameterName) {
        return new SingleStringUriParameter("test");
    }

    @Override
    public ParameterValueConverter<String> getTypeConverter() {
        return StringParameterValueConverter.INSTANCE;
    }

    @Override
    public String getTestValue() {
        return "value";
    }

    @Override
    public String getDefaultValue() {
        return "default";
    }
}
