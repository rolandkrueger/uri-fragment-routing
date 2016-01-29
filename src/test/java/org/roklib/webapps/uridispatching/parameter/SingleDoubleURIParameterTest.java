package org.roklib.webapps.uridispatching.parameter;

public class SingleDoubleURIParameterTest extends AbstractSingleURIParameterTest<Double> {
    @Override
    public AbstractSingleURIParameter<Double> getTestSingleURIParameter(String parameterName) {
        return new SingleDoubleURIParameter("test");
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
