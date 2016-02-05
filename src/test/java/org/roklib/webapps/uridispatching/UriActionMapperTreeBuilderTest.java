package org.roklib.webapps.uridispatching;

import org.junit.Test;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.*;

public class UriActionMapperTreeBuilderTest {

    private UriActionMapperTree mapperTree;

    @Test
    public void test_build_gives_empty_mapper_tree() {
        mapperTree = create().build();

        assertThat(mapperTree, notNullValue());
        assert_number_of_root_path_segment_mappers(mapperTree, 0);
    }

    @Test
    public void test_add_two_action_mapper_to_root() {
        // @formatter:off
        mapperTree = create().map(
                pathSegment("home").on(action(HomeActionCommand.class)))
                .map(
                        pathSegment("admin").on(action(AdminActionCommand.class))
                )
                .build();
        // @formatter:on

        assert_number_of_root_path_segment_mappers(mapperTree, 2);

        assert_that_fragment_resolves_to_action("home", HomeActionCommand.class);
        assert_that_fragment_resolves_to_action("/admin", AdminActionCommand.class);
    }

    @Test
    public void test_add_subtree_mapper_to_root() {
        // @formatter:off
        mapperTree = create().map(
                pathSegment("subtree").on(
                        subtree()
                                .map(pathSegment("home").on(action(HomeActionCommand.class)))
                                .map(pathSegment("admin").on(action(AdminActionCommand.class)))
                )
        ).build();
        // @formatter:on

        assert_number_of_root_path_segment_mappers(mapperTree, 1);

        assert_that_fragment_resolves_to_action("subtree/home", HomeActionCommand.class);
        assert_that_fragment_resolves_to_action("/subtree/admin", AdminActionCommand.class);
    }

    @Test
    public void test_set_action_command_to_subtree_mapper() {
        // formatter:off
        mapperTree = create().map(
                pathSegment("admin").on(subtree().withActionCommand(AdminActionCommand.class))
        ).build();
        // formatter:on

        assert_that_fragment_resolves_to_action("/admin", AdminActionCommand.class);
    }

    private void assert_that_mapper_is_correct(final AbstractUriPathSegmentActionMapper actualMapper, String expectedSegmentName, Class<?> expectedClass, Class<? extends UriActionCommand> expectedCommand) {
        assertThat(actualMapper, instanceOf(expectedClass));
        assertThat(actualMapper.getMapperName(), is(equalTo(expectedSegmentName)));
        assertThat(actualMapper.getActionCommand(), is(equalTo(expectedCommand)));
    }

    private void assert_number_of_root_path_segment_mappers(final UriActionMapperTree mapperTree, final int number) {
        assertThat(mapperTree.getRootActionMappers(), hasSize(number));
    }

    private void assert_that_fragment_resolves_to_action(String fragment, Class<? extends UriActionCommand> expectedCommandMock) {
        mapperTree.interpretFragment(fragment);
        // FIXME
        //verify(expectedCommandMock).execute();
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