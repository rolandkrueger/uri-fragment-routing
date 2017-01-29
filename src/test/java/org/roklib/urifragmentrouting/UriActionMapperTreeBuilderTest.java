package org.roklib.urifragmentrouting;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.CurrentUriFragment;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.strategy.QueryParameterExtractionStrategy;
import org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.roklib.urifragmentrouting.UriActionMapperTree.create;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeBuilderTest {

    private final static Logger LOG = LoggerFactory.getLogger(UriActionMapperTreeBuilderTest.class);

    @Mock
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;
    @Mock
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriActionMapperTree mapperTree;

    @After
    public void printMapperTree() {
        if (mapperTree != null) {
            LOG.info("Using following mapper tree:");
            mapperTree.getMapperOverview().stream().sorted().forEach(LOG::info);
            LOG.info("----------------------------------------");
        }
    }

    @Test
    public void test_create_returns_builder() {
        assertThat(create(), isA(UriActionMapperTree.UriActionMapperTreeBuilder.class));
    }

    @Test
    public void test_set_a_custom_UriTokenExtractionStrategy() {
        when(uriTokenExtractionStrategy.extractUriTokens(anyString())).thenReturn(new LinkedList<>(Collections.singletonList("replaced")));

        mapperTree = create().useUriTokenExtractionStrategy(uriTokenExtractionStrategy)
                .buildMapperTree()
                .map("replaced").onAction(SomeActionCommand.class)
                .finishMapper().build();

        final UriActionCommand command = mapperTree.interpretFragment("fragment");

        assertThat(command, instanceOf(SomeActionCommand.class));
        final SomeActionCommand resolvedCommand = (SomeActionCommand) command;
        // the currently interpreted URI fragment cannot be replaced by the UriTokenExtractionStrategy
        assertThat(resolvedCommand.currentUriFragment, is(equalTo("fragment")));
        assertThat(resolvedCommand.isExecuted, is(true));
        verify(uriTokenExtractionStrategy).extractUriTokens("fragment");
    }

    @Test(expected = NullPointerException.class)
    public void test_set_null_UriTokenExtractionStrategy() {
        create().useUriTokenExtractionStrategy(null)
                .buildMapperTree().build();
    }

    @Test
    public void test_set_a_custom_QueryParameterExtractionStrategy() {
        when(queryParameterExtractionStrategy.extractQueryParameters(anyString())).thenReturn(Collections.emptyMap());
        when(queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(anyString())).thenReturn("stripped");
        when(uriTokenExtractionStrategy.extractUriTokens(anyString())).thenReturn(new LinkedList<>());

        mapperTree = create()
                .useUriTokenExtractionStrategy(uriTokenExtractionStrategy)
                .useQueryParameterExtractionStrategy(queryParameterExtractionStrategy)
                .buildMapperTree().build();
        mapperTree.interpretFragment("fragment");

        verify(queryParameterExtractionStrategy).extractQueryParameters("fragment");
        verify(queryParameterExtractionStrategy).stripQueryParametersFromUriFragment("fragment");
        verify(uriTokenExtractionStrategy).extractUriTokens("stripped");
    }

    @Test
    public void testSetParameterMode() {
        mapperTree = create().useParameterMode(ParameterMode.DIRECTORY)
                .buildMapperTree()
                .map("fragment").onAction(SomeActionCommand.class)
                .withSingleValuedParameter("id").forType(Integer.class).noDefault()
                .withSingleValuedParameter("lang").forType(String.class).noDefault()
                .finishMapper()
                .build();

        final SomeActionCommand command = (SomeActionCommand) mapperTree.interpretFragment("fragment/17/de");
        assertThat(command.parameterValues.hasValueFor("fragment", "id"), is(true));
        assertThat(command.parameterValues.getValueFor("fragment", "id").getValue(), is(17));
        assertThat(command.parameterValues.hasValueFor("fragment", "lang"), is(true));
        assertThat(command.parameterValues.getValueFor("fragment", "lang").getValue(), is("de"));
    }

    @Test(expected = NullPointerException.class)
    public void test_set_null_QueryParameterExtractionStrategy() {
        create().useQueryParameterExtractionStrategy(null)
                .buildMapperTree().build();
    }

    @Test
    public void test_empty_mapper_tree_when_calling_build_after_create() {
        mapperTree = create()
                .buildMapperTree().build();

        assertThat(mapperTree, notNullValue());
        assert_number_of_root_path_segment_mappers(mapperTree, 0);
    }

    @Test
    public void test_add_two_action_mapper_to_root() {
        mapperTree = create().buildMapperTree()
                .map("home").onAction(HomeActionCommand.class).finishMapper()
                .map("admin").onAction(AdminActionCommand.class).finishMapper()
                .build();

        assert_number_of_root_path_segment_mappers(mapperTree, 2);

        assert_that_fragment_resolves_to_action("home", HomeActionCommand.class);
        assert_that_fragment_resolves_to_action("/admin", AdminActionCommand.class);
    }

    @Test
    public void test_add_subtree_mapper_to_root() {
        // @formatter:off
        mapperTree = create()
                .buildMapperTree()
                .mapSubtree("subtree")
                .onSubtree()
                .map("home").onAction(HomeActionCommand.class).finishMapper()
                .map("admin").onAction(AdminActionCommand.class).finishMapper()
                .finishMapper()
                .build();
        // @formatter:on

        assert_number_of_root_path_segment_mappers(mapperTree, 1);

        assert_that_fragment_resolves_to_action("subtree/home", HomeActionCommand.class);
        assert_that_fragment_resolves_to_action("/subtree/admin", AdminActionCommand.class);
    }

    @Test
    public void test_set_action_command_to_subtree_mapper() {
        // formatter:off
        mapperTree = create().buildMapperTree()
                .mapSubtree("admin").onAction(AdminActionCommand.class).onSubtree()
                .build();
        // formatter:on

        assert_that_fragment_resolves_to_action("/admin", AdminActionCommand.class);
    }

    @Test
    public void test_action_command_on_root_mapper() throws Exception {
        mapperTree = create().setRootActionCommand(SomeActionCommand.class).buildMapperTree().build();
        assert_that_fragment_resolves_to_action("", SomeActionCommand.class);
        assert_that_fragment_resolves_to_action("/", SomeActionCommand.class);
    }

    private void assert_number_of_root_path_segment_mappers(final UriActionMapperTree mapperTree, final int number) {
        assertThat(mapperTree.getFirstLevelActionMappers(), hasSize(number));
    }

    private void assert_that_fragment_resolves_to_action(final String fragment, final Class<? extends UriActionCommand> expectedCommand) {
        final UriActionCommand command = mapperTree.interpretFragment(fragment);
        assertThat(command, instanceOf(expectedCommand));
    }

    public static class SomeActionCommand implements UriActionCommand {

        private String currentUriFragment;
        private boolean isExecuted = false;
        private CapturedParameterValues parameterValues;

        @CurrentUriFragment
        public void setCurrentUriFragment(final String currentUriFragment) {
            this.currentUriFragment = currentUriFragment;
        }

        @AllCapturedParameters
        public void setParameterValues(final CapturedParameterValues parameterValues) {
            this.parameterValues = parameterValues;
        }

        @Override
        public void run() {
            isExecuted = true;
        }
    }

    public static class HomeActionCommand implements UriActionCommand {
        @Override
        public void run() {
        }
    }

    public static class AdminActionCommand implements UriActionCommand {
        @Override
        public void run() {
        }
    }

}