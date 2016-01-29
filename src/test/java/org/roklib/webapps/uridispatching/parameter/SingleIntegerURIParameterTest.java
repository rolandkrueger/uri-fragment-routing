package org.roklib.webapps.uridispatching.parameter;

public class SingleIntegerURIParameterTest extends AbstractSingleURIParameterTest<Integer> {
    @Override
    public AbstractSingleURIParameter<Integer> getTestSingleURIParameter(String parameterName) {
        SingleIntegerURIParameter result = new SingleIntegerURIParameter("test");
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
