package org.roklib.urifragmentrouting.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.SingleLongUriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

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
        final CapturedParameterValues capturedParameterValues = new CapturedParameterValues();
        mapper.interpretTokensImpl(capturedParameterValues,
                "123",
                Collections.emptyList(),
                Collections.emptyMap(),
                ParameterMode.DIRECTORY);

        assertThat(capturedParameterValues.hasValueFor("mapperName", "value"), is(true));
        assertThat(capturedParameterValues.getValueFor("mapperName", "value").getValue(), is(123L));
    }

    @Test
    public void action_mapper_is_responsible_for_all_tokens() throws Exception {
        assertThat(mapper.isResponsibleForToken(""), is(true));
        assertThat(mapper.isResponsibleForToken("token"), is(true));
        assertThat(mapper.isResponsibleForToken(null), is(true));
    }
}
