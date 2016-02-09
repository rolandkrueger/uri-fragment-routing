package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class RegexUriPathSegmentActionMapperTest {

    private RegexUriPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new RegexUriPathSegmentActionMapper("regexMapper",  "values", new AbstractRegexToStringListParameterValueConverter("(\\d+)xxx(\\d+)") {
            @Override
            public String convertToString(List<String> value) {
                return null;
            }
        });
    }

    @Test
    public void capturing_groups_go_into_uri_parameter() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "123xxx456",
                Collections.emptyList(),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(capturedParameterValues.hasValueFor("regexMapper", "values"), is(true));
        final ParameterValue<List<String>> values = capturedParameterValues.getValueFor("regexMapper", "values");
        assertThat(values.getValue(), hasItems("123", "456"));
        assertThat(values.getValue(), hasSize(2));
    }

    @Test
    public void test_dispatches_to_sub_mapper() {
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("aaa"));
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("bbb", BBBActionCommand.class));
        final Class<? extends UriActionCommand> resultClass = mapper.interpretTokensImpl(new CapturedParameterValuesImpl(),
                "123xxx456",
                new ArrayList<>(Collections.singletonList("bbb")),
                Collections.emptyMap(),
                UriPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(resultClass, is(equalTo(BBBActionCommand.class)));
    }

    @Test
    public void testIsResponsibleFor() {
        assertThat(mapper.isResponsibleForToken("123xxx456"), is(true));
        assertThat(mapper.isResponsibleForToken("123456"), is(false));
    }

    public static class BBBActionCommand implements UriActionCommand {
        @Override
        public void run() {
        }
    }
}
