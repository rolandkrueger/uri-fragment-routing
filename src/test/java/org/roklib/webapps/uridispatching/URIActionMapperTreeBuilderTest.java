/*
 * Copyright (C) 2007 - 2014 Roland Krueger
 *
 * Author: Roland Krueger (www.rolandkrueger.info)
 *
 * This file is part of RoKlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.roklib.webapps.uridispatching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.verify;
import static org.roklib.webapps.uridispatching.URIActionMapperTree.*;

@RunWith(MockitoJUnitRunner.class)
public class URIActionMapperTreeBuilderTest {

    @Mock
    private AbstractURIActionCommand homeCommandMock;
    @Mock
    private AbstractURIActionCommand adminCommandMock;
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
        assert_that_mapper_is_correct(mapperTree.getRootActionMapper("home"), "home", org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper.class, homeCommandMock);
        assert_that_mapper_is_correct(mapperTree.getRootActionMapper("admin"), "admin", org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper.class, adminCommandMock);

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

        final Map<String, org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper> subtreeMapperMap = mapperTree.getRootActionMapper("subtree").getSubMapperMap();
        assertThat(subtreeMapperMap.size(), is(2));
        assert_that_mapper_is_correct(subtreeMapperMap.get("home"), "home", org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper.class, homeCommandMock);
        assert_that_mapper_is_correct(subtreeMapperMap.get("admin"), "admin", org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper.class, adminCommandMock);

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

        assertThat(mapperTree.getRootActionMapper("admin").getActionCommand(), is(adminCommandMock));
        assert_that_fragment_resolves_to_action("/admin", adminCommandMock);
    }

    private void assert_that_mapper_is_correct(final org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper actualMapper, String expectedSegmentName, Class<?> expectedClass, AbstractURIActionCommand expectedCommand) {
        assertThat(actualMapper, instanceOf(expectedClass));
        assertThat(actualMapper.getActionName(), equalTo(expectedSegmentName));
        assertThat(actualMapper.getActionCommand(), equalTo(expectedCommand));
    }

    private void assert_number_of_root_path_segment_mappers(final URIActionMapperTree mapperTree, final int number) {
        assertThat(mapperTree.getRootActionMappers(), hasSize(number));
    }

    private void assert_that_fragment_resolves_to_action(String fragment, AbstractURIActionCommand expectedCommandMock) {
        mapperTree.interpretFragment(fragment);
        verify(expectedCommandMock).execute();
    }
}