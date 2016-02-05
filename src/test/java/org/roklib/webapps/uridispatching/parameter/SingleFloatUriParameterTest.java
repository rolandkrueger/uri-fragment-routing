package org.roklib.webapps.uridispatching.parameter;

public class SingleFloatUriParameterTest extends AbstractSingleUriParameterTest<Float> {
    @Override
    public AbstractSingleUriParameter<Float> getTestSingleURIParameter(String parameterName) {
        return new SingleFloatUriParameter("test");
    }

    @Override
    public Float getTestValue() {
        return 23.0f;
    }

    @Override
    public Float getDefaultValue() {
        return 17.0f;
    }
}
