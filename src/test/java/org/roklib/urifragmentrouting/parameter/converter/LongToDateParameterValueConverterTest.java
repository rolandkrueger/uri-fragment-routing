package org.roklib.urifragmentrouting.parameter.converter;

import java.util.Date;

public class LongToDateParameterValueConverterTest extends AbstractParameterValueConverterTest<Date> {

    private final Date date = new Date();

    @Override
    protected ParameterValueConverter<Date> getConverter() {
        return LongToDateParameterValueConverter.INSTANCE;
    }

    @Override
    protected Date getExpectedValue() {
        return date;
    }

    @Override
    protected String getStringForExpectedValue() {
        return String.valueOf(date.getTime());
    }

    @Override
    protected String getInvalidStringValue() {
        return "string";
    }
}
