package org.roklib.webapps.uridispatching.parameter;


public class SingleLongWithIgnoredTextURIParameterTest extends AbstractSingleURIParameterTest<Long> {
    @Override
    public AbstractSingleURIParameter<Long> getTestSingleURIParameter(String parameterName) {
        return new SingleLongWithIgnoredTextURIParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "1234-test";
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
