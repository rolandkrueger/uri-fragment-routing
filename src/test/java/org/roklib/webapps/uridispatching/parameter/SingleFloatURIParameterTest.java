package org.roklib.webapps.uridispatching.parameter;

public class SingleFloatURIParameterTest extends AbstractSingleURIParameterTest<Float> {
    @Override
    public AbstractSingleURIParameter<Float> getTestSingleURIParameter(String parameterName) {
        return new SingleFloatURIParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "23.000";
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
