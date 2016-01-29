package org.roklib.webapps.uridispatching.parameter;

public class SingleBooleanURIParameterTest extends AbstractSingleURIParameterTest<Boolean> {
    @Override
    public AbstractSingleURIParameter<Boolean> getTestSingleURIParameter(String parameterName) {
        return new SingleBooleanURIParameter("test");
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
