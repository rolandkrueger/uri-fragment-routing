package org.roklib.urifragmentrouting.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValuesImpl;
import org.roklib.urifragmentrouting.parameter.SingleLongUriParameter;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CatchAllUriPathSegmentActionMapperTest {

    private CatchAllUriPathSegmentActionMapper<Long> mapper;

    @Before
    public void setUp() {
        mapper = new CatchAllUriPathSegmentActionMapper<>("mapperName", new SingleLongUriParameter("value"));
    }

    @Test
    public void test() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "123",
                Collections.emptyList(),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(capturedParameterValues.hasValueFor("mapperName", "value"), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "value").getValue(), is(123L));
    }
}
