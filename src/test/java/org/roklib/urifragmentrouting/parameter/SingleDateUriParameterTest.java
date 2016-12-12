package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.LongToDateParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

public class SingleDateUriParameterTest extends AbstractSingleUriParameterTest<Date> {

    @Override
    public Date getTestValue() {
        LocalDate localDate = LocalDate.of(2014, Month.SEPTEMBER, 8);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Date getDefaultValue() {
        return new Date();
    }

    @Override
    public AbstractSingleUriParameter<Date> getTestSingleURIParameter(String parameterName) {
        return new SingleDateUriParameter(parameterName);
    }

    @Override
    public ParameterValueConverter<Date> getTypeConverter() {
        return LongToDateParameterValueConverter.INSTANCE;
    }
}