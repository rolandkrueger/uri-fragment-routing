package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.ISO8601ToLocalDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.time.LocalDate;
import java.time.Month;

public class SingleLocalDateUriParameterTest extends AbstractSingleUriParameterTest<LocalDate> {

    @Override
    public LocalDate getTestValue() {
        return LocalDate.of(2014, Month.SEPTEMBER, 8);
    }

    @Override
    public LocalDate getDefaultValue() {
        return LocalDate.now();
    }

    @Override
    public AbstractSingleUriParameter<LocalDate> getTestSingleURIParameter(String parameterName) {
        return new SingleLocalDateUriParameter(parameterName);
    }

    @Override
    public ParameterValueConverter<LocalDate> getTypeConverter() {
        return ISO8601ToLocalDateParameterValueConverter.INSTANCE;
    }
}