package org.roklib.webapps.uridispatching.parameter;

public class SingleStringURIParameterTest extends AbstractSingleURIParameterTest<String> {

    @Override
    public AbstractSingleURIParameter<String> getTestSingleURIParameter(String parameterName) {
        return new SingleStringURIParameter("test");
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
