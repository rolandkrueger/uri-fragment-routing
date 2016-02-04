package org.roklib.webapps.uridispatching.parameter;

public class SingleStringUriParameterTest extends AbstractSingleUriParameterTest<String> {

    @Override
    public AbstractSingleUriParameter<String> getTestSingleURIParameter(String parameterName) {
        return new SingleStringUriParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "value";
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
