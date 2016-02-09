package org.roklib.webapps.uridispatching.mapper;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StartsWithUriPathSegmentActionMapperTest {
    private StartsWithUriPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new StartsWithUriPathSegmentActionMapper("mapperName", "id_", "parameter");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_captured_parameter() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "id_17",
                Collections.emptyList(),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);
        assertThat(capturedParameterValues.hasValueFor("mapperName", "parameter"), is(true));
        assertThat((List<String>) capturedParameterValues.getValueFor("mapperName", "parameter").getValue(), IsIterableContainingInOrder.contains("17"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new StartsWithUriPathSegmentActionMapper("mapperName", "  ", "value");
    }
}
