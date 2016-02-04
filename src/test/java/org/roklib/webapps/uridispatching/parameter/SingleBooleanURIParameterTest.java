package org.roklib.webapps.uridispatching.parameter;

public class SingleBooleanUriParameterTest extends AbstractSingleUriParameterTest<Boolean> {
    @Override
    public AbstractSingleUriParameter<Boolean> getTestSingleURIParameter(String parameterName) {
        return new SingleBooleanUriParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "true";
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
