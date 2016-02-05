package org.roklib.webapps.uridispatching.parameter;


public class SingleLongWithIgnoredTextUriParameterTest extends AbstractSingleUriParameterTest<Long> {
    @Override
    public AbstractSingleUriParameter<Long> getTestSingleURIParameter(String parameterName) {
        return new SingleLongWithIgnoredTextUriParameter("test");
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
