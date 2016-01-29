package org.roklib.webapps.uridispatching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.roklib.webapps.uridispatching.URIActionMapperTree.*;

@RunWith(MockitoJUnitRunner.class)
public class URIActionMapperTreeBuilderTest {

    @Mock
    private Class<? extends URIActionCommand> homeCommandMock;
    @Mock
    private Class<? extends URIActionCommand> adminCommandMock;
    private URIActionMapperTree mapperTree;

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
            pathSegment("home").on(action(homeCommandMock)))
            .map(
                pathSegment("admin").on(action(adminCommandMock))
            )
            .build();
        // @formatter:on

        assertThat(mapperTree.getRootActionMapper("undefined"), nullValue());

        assert_number_of_root_path_segment_mappers(mapperTree, 2);
        assert_that_mapper_is_correct(mapperTree.getRootActionMapper("home"), "home", SimpleURIPathSegmentActionMapper.class, homeCommandMock);
        assert_that_mapper_is_correct(mapperTree.getRootActionMapper("admin"), "admin", SimpleURIPathSegmentActionMapper.class, adminCommandMock);

        assert_that_fragment_resolves_to_action("home", homeCommandMock);
        assert_that_fragment_resolves_to_action("/admin", adminCommandMock);
    }

    @Test
    public void test_add_subtree_mapper_to_root() {
        // @formatter:off
        mapperTree = create().map(
            pathSegment("subtree").on(
                subtree()
                    .map(pathSegment("home").on(action(homeCommandMock)))
                    .map(pathSegment("admin").on(action(adminCommandMock)))
            )
        ).build();
        // @formatter:on

        assert_number_of_root_path_segment_mappers(mapperTree, 1);

        final Map<String, AbstractURIPathSegmentActionMapper> subtreeMapperMap = mapperTree.getRootActionMapper("subtree").getSubMapperMap();
        assertThat(subtreeMapperMap.size(), is(2));
        assert_that_mapper_is_correct(subtreeMapperMap.get("home"), "home", SimpleURIPathSegmentActionMapper.class, homeCommandMock);
        assert_that_mapper_is_correct(subtreeMapperMap.get("admin"), "admin", SimpleURIPathSegmentActionMapper.class, adminCommandMock);

        assert_that_fragment_resolves_to_action("subtree/home", homeCommandMock);
        assert_that_fragment_resolves_to_action("/subtree/admin", adminCommandMock);
    }

    @Test
    public void test_set_action_command_to_subtree_mapper() {
        // formatter:off
        mapperTree = create().map(
            pathSegment("admin").on(subtree().withActionCommand(adminCommandMock))
        ).build();
        // formatter:on

        // FIXME
//        assertThat(mapperTree.getRootActionMapper("admin").getActionCommand(), is(adminCommandMock));
        assert_that_fragment_resolves_to_action("/admin", adminCommandMock);
    }

    private void assert_that_mapper_is_correct(final AbstractURIPathSegmentActionMapper actualMapper, String expectedSegmentName, Class<?> expectedClass, Class<? extends URIActionCommand> expectedCommand) {
        assertThat(actualMapper, instanceOf(expectedClass));
        assertThat(actualMapper.getMapperName(), equalTo(expectedSegmentName));
        assertThat(actualMapper.getActionCommand(), equalTo(expectedCommand));
    }

    private void assert_number_of_root_path_segment_mappers(final URIActionMapperTree mapperTree, final int number) {
        assertThat(mapperTree.getRootActionMappers(), hasSize(number));
    }

    private void assert_that_fragment_resolves_to_action(String fragment, Class<? extends URIActionCommand> expectedCommandMock) {
        mapperTree.interpretFragment(fragment);
        // FIXME
        //verify(expectedCommandMock).execute();
    }
}