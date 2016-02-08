package org.roklib.webapps.uridispatching.parameter;


public class SingleLongWithIgnoredTextUriParameterTest extends AbstractSingleUriParameterTest<SingleLongWithIgnoredTextUriParameter.IdWithText> {
    @Override
    public AbstractSingleUriParameter<SingleLongWithIgnoredTextUriParameter.IdWithText> getTestSingleURIParameter(String parameterName) {
        return new SingleLongWithIgnoredTextUriParameter("test");
    }

    @Override
    public SingleLongWithIgnoredTextUriParameter.IdWithText getTestValue() {
        return new SingleLongWithIgnoredTextUriParameter.IdWithTextImpl(17L, "test");
    }

    @Override
    public SingleLongWithIgnoredTextUriParameter.IdWithText getDefaultValue() {
        return new SingleLongWithIgnoredTextUriParameter.IdWithTextImpl(23L, "default");
    }

}
