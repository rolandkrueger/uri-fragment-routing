package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

public class SingleLongWithIgnoredTextUriParameterTest extends AbstractSingleUriParameterTest<SingleLongWithIgnoredTextUriParameter.IdWithText> {
    @Override
    public AbstractSingleUriParameter<SingleLongWithIgnoredTextUriParameter.IdWithText> getTestSingleURIParameter(String parameterName) {
        return new SingleLongWithIgnoredTextUriParameter("test");
    }

    @Override
    public ParameterValueConverter<SingleLongWithIgnoredTextUriParameter.IdWithText> getTypeConverter() {
        return SingleLongWithIgnoredTextUriParameter.IdWithTextParameterValueConverter.INSTANCE;
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
