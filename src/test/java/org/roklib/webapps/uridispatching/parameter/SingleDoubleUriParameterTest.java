package org.roklib.webapps.uridispatching.parameter;

public class SingleDoubleUriParameterTest extends AbstractSingleUriParameterTest<Double> {
    @Override
    public AbstractSingleUriParameter<Double> getTestSingleURIParameter(String parameterName) {
        return new SingleDoubleUriParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "17.0";
    }

    @Override
    public Double getTestValue() {
        return 17.0d;
    }

    @Override
    public Double getDefaultValue() {
        return 23.0d;
    }
}
