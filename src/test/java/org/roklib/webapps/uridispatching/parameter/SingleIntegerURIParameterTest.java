package org.roklib.webapps.uridispatching.parameter;

public class SingleIntegerUriParameterTest extends AbstractSingleUriParameterTest<Integer> {
    @Override
    public AbstractSingleUriParameter<Integer> getTestSingleURIParameter(String parameterName) {
        SingleIntegerUriParameter result = new SingleIntegerUriParameter("test");
        return result;
    }

    @Override
    public String getTestValueAsString() {
        return "123";
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
