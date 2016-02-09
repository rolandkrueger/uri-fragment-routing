package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.converter.DoubleParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;

public class SingleDoubleUriParameterTest extends AbstractSingleUriParameterTest<Double> {
    @Override
    public AbstractSingleUriParameter<Double> getTestSingleURIParameter(String parameterName) {
        return new SingleDoubleUriParameter("test");
    }

    @Override
    public ParameterValueConverter<Double> getTypeConverter() {
        return DoubleParameterValueConverter.INSTANCE;
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
