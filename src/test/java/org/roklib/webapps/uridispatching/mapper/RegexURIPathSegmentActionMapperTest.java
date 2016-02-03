package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class RegexURIPathSegmentActionMapperTest {

    private RegexURIPathSegmentActionMapper mapper;

    @Before
    public void setUp() {
        mapper = new RegexURIPathSegmentActionMapper("regexMapper", "(\\d+)xxx(\\d+)", "values");
    }

    @Test
    public void capturing_groups_go_into_uri_parameter() {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        mapper.interpretTokensImpl(capturedParameterValues,
                "123xxx456",
                Collections.emptyList(),
                Collections.emptyMap(),
                URIPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(capturedParameterValues.hasValueFor("regexMapper", "values"), is(true));
        final ParameterValue<List<String>> values = capturedParameterValues.getValueFor("regexMapper", "values");
        assertThat(values.getValue(), hasItems("123", "456"));
        assertThat(values.getValue(), hasSize(2));
    }

    @Test
    public void test_dispatches_to_sub_mapper() {
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("aaa"));
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("bbb", BBBActionCommand.class));
        final Class<? extends URIActionCommand> resultClass = mapper.interpretTokensImpl(new CapturedParameterValuesImpl(),
                "123xxx456",
                new ArrayList<>(Collections.singletonList("bbb")),
                Collections.emptyMap(),
                URIPathSegmentActionMapper.ParameterMode.DIRECTORY);

        assertThat(resultClass, is(equalTo(BBBActionCommand.class)));
    }

    @Test
    public void testIsResponsibleFor() {
        assertThat(mapper.isResponsibleForToken("123xxx456"), is(true));
        assertThat(mapper.isResponsibleForToken("123456"), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty_regex_throws_exception() {
        mapper = new RegexURIPathSegmentActionMapper("mapper", " ", "id");
    }

    @Test(expected = PatternSyntaxException.class)
    public void invalid_regex_throws_exception() {
        mapper = new RegexURIPathSegmentActionMapper("mapper", ".*[", "id");
    }

    public static class BBBActionCommand implements URIActionCommand {
        @Override
        public void execute() {
        }
    }
}
