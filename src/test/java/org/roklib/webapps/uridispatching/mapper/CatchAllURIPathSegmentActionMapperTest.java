package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CatchAllURIPathSegmentActionMapperTest {

    private CatchAllURIPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new CatchAllURIPathSegmentActionMapper("mapperName", "value");
    }

    @Test
    public void test() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "currentUriToken",
                Collections.emptyList(),
                Collections.emptyMap(),
                URIPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(capturedParameterValues.hasValueFor("mapperName", "value"), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "value").getValue(), is("currentUriToken"));
    }
}
