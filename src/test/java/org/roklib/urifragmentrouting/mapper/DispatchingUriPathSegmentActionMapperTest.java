package org.roklib.urifragmentrouting.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DispatchingUriPathSegmentActionMapperTest {

    private DispatchingUriPathSegmentActionMapper mapper;
    private LinkedList<String> uriTokens;

    @Mock
    private CapturedParameterValues capturedParameterValues;
    private SimpleUriPathSegmentActionMapper submapper;

    @Before
    public void setUp() throws Exception {
        mapper = new DispatchingUriPathSegmentActionMapper("base");
        uriTokens = new LinkedList<>(Collections.singletonList("submapper"));
        submapper = new SimpleUriPathSegmentActionMapper("submapper");
        submapper.setActionCommandClass(ActionCommandForTest.class);
    }

    @Test
    public void test_uri_interpretation_without_sub_mappers() {
        UriActionCommand result = doInterpretTokens(uriTokens);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void test_uri_interpretation_with_sub_mappers_but_unknown_mapper_name() {
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("sub1"));
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("sub2"));

        UriActionCommand result = doInterpretTokens(uriTokens);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void test_successful_forwarding() {
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("sub1"));
        mapper.addSubMapper(submapper);
        mapper.addSubMapper(new SimpleUriPathSegmentActionMapper("sub2"));

        UriActionCommand result = doInterpretTokens(uriTokens);
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test
    public void test_action_class_is_returned_when_uri_token_list_is_empty() {
        mapper.setActionCommandClass(ActionCommandForTest.class);

        UriActionCommand result = doInterpretTokens(Collections.emptyList());
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test
    public void test_sub_mapper_is_starts_with_mapper() {
        StartsWithUriPathSegmentActionMapper startsWithMapper = new StartsWithUriPathSegmentActionMapper("mapper", "id_", "value");
        startsWithMapper.setActionCommandClass(ActionCommandForTest.class);
        mapper.addSubMapper(startsWithMapper);

        uriTokens.clear();
        uriTokens.add("id_17");

        UriActionCommand result = doInterpretTokens(uriTokens);
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addSubMapper_with_submappers_parent_already_set() {
        DispatchingUriPathSegmentActionMapper otherParent = new DispatchingUriPathSegmentActionMapper("other");
        otherParent.addSubMapper(submapper);
        mapper.addSubMapper(submapper);
    }

    @Test
    public void test_empty_uri_token_is_skipped() {
        mapper.addSubMapper(submapper);
        uriTokens.addFirst("");

        UriActionCommand result = doInterpretTokens(uriTokens);
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test
    public void testIsResponsibleFor() {
        assertThat(mapper.isResponsibleForToken("base"), is(true));
        mapper = new DispatchingUriPathSegmentActionMapper("mapperName", "pathSegment");
        assertThat(mapper.isResponsibleForToken("pathSegment"), is(true));
        assertThat(mapper.isResponsibleForToken("mapperName"), is(false));
    }

    private void assertThatCorrectActionClassIsReturned(UriActionCommand result) {
        assertThat("action command class is null", result, is(notNullValue()));
        assertThat(result.getClass().getName(), is(ActionCommandForTest.class.getName()));
    }

    @SuppressWarnings("unchecked")
    private UriActionCommand doInterpretTokens(List<String> uriTokens) {
        UriActionCommandFactory uriActionCommandFactory = mapper.interpretTokens(capturedParameterValues, "", uriTokens, Collections.emptyMap(), ParameterMode.QUERY);
        if (uriActionCommandFactory != null) {
            return uriActionCommandFactory.createUriActionCommand();
        }
        return null;
    }

    public static class ActionCommandForTest implements UriActionCommand {
        @Override
        public void run() {
        }
    }
}