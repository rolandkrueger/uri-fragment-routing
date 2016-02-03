package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;
import org.roklib.webapps.uridispatching.parameter.URIParameterError;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StartsWithURIPathSegmentActionMapperTest {
    private StartsWithURIPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new StartsWithURIPathSegmentActionMapper("mapperName", "id_", new SingleIntegerURIParameter("parameter"));
    }

    @Test
    public void test_captured_parameter() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "id_17",
                Collections.emptyList(),
                Collections.emptyMap(),
                URIPathSegmentActionMapper.ParameterMode.DIRECTORY);
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
                URIPathSegmentActionMapper.ParameterMode.DIRECTORY);
        assertThat(capturedParameterValues.getValueFor("mapperName", "parameter").hasError(), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "parameter").getError(), is(URIParameterError.CONVERSION_ERROR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new StartsWithURIPathSegmentActionMapper("mapperName", "  ", new SingleStringURIParameter("value"));
    }
}
