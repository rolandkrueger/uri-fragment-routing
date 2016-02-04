package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerUriParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringUriParameter;
import org.roklib.webapps.uridispatching.parameter.UriParameterError;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StartsWithUriPathSegmentActionMapperTest {
    private StartsWithUriPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new StartsWithUriPathSegmentActionMapper("mapperName", "id_", new SingleIntegerUriParameter("parameter"));
    }

    @Test
    public void test_captured_parameter() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "id_17",
                Collections.emptyList(),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);
        assertThat(capturedParameterValues.hasValueFor("mapperName", "parameter"), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "parameter").getValue(), is(17));
    }

    @Test
    public void captured_parameter_with_incorrect_type() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "id_seventeen",
                Collections.emptyList(),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);
        assertThat(capturedParameterValues.getValueFor("mapperName", "parameter").hasError(), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "parameter").getError(), is(UriParameterError.CONVERSION_ERROR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new StartsWithUriPathSegmentActionMapper("mapperName", "  ", new SingleStringUriParameter("value"));
    }
}
