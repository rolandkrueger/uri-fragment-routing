package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.ConsumedParameterValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
@RunWith(MockitoJUnitRunner.class)
public class DispatchingURIPathSegmentActionMapperTest {

    private DispatchingURIPathSegmentActionMapper mapper;
    private List<String> uriTokens;

    @Mock
    private ConsumedParameterValues consumedParameterValues;
    private SimpleURIPathSegmentActionMapper submapper;

    @Before
    public void setUp() throws Exception {
        mapper = new DispatchingURIPathSegmentActionMapper("base");
        uriTokens = new ArrayList<>(Collections.singletonList("submapper"));
        submapper = new SimpleURIPathSegmentActionMapper("submapper");
        submapper.setActionCommandClass(ActionCommandMock.class);
    }

    @Test
    public void test_uri_interpretation_without_sub_mappers() {
        Class<? extends URIActionCommand> result = doInterpretTokens(uriTokens);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void test_uri_interpretation_with_sub_mappers_but_unknown_mapper_name() {
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("sub1"));
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("sub2"));

        Class<? extends URIActionCommand> result = doInterpretTokens(uriTokens);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void test_successful_forwarding() {
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("sub1"));
        mapper.addSubMapper(submapper);
        mapper.addSubMapper(new SimpleURIPathSegmentActionMapper("sub2"));

        Class<? extends URIActionCommand> result = doInterpretTokens(uriTokens);
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test
    public void test_action_class_is_returned_when_uri_token_list_is_empty() {
        mapper.setActionCommandClass(ActionCommandMock.class);

        Class<? extends URIActionCommand> result = doInterpretTokens(Collections.emptyList());
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test
    public void test_sub_mapper_is_starts_with_mapper() {
        StartsWithURIPathSegmentActionMapper startsWithMapper = new StartsWithURIPathSegmentActionMapper("id_");
        startsWithMapper.setActionCommandClass(ActionCommandMock.class);
        mapper.addSubMapper(startsWithMapper);

        uriTokens.clear();
        uriTokens.add("id_17");

        Class<? extends URIActionCommand> result = doInterpretTokens(uriTokens);
        assertThatCorrectActionClassIsReturned(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_addSubMapper_with_submappers_parent_already_set() {
        DispatchingURIPathSegmentActionMapper otherParent = new DispatchingURIPathSegmentActionMapper("other");
        otherParent.addSubMapper(submapper);
        mapper.addSubMapper(submapper);
    }

    private void assertThatCorrectActionClassIsReturned(Class<? extends URIActionCommand> result) {
        assertThat(result.getName(), is(ActionCommandMock.class.getName()));
    }

    private Class<? extends URIActionCommand> doInterpretTokens(List<String> uriTokens) {
        return mapper.interpretTokens(consumedParameterValues, uriTokens, Collections.emptyMap(), URIPathSegmentActionMapper.ParameterMode.QUERY);
    }

    private static class ActionCommandMock implements URIActionCommand {
        @Override
        public void execute() {
        }
    }
}