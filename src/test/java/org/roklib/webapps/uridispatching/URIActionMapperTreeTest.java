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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.roklib.webapps.uridispatching.URIActionMapperTree.*;

@RunWith(MockitoJUnitRunner.class)
public class URIActionMapperTreeTest {

    @Mock
    private AbstractURIActionCommand homeCommandMock;

    @Test
    public void test_create_returns_builder() {
        assertThat(create(), isA(URIActionMapperTreeBuilder.class));
    }

    @Test
    public void test_getRootActionMapper() {
        // @formatter:off
        final URIActionMapperTree tree = create()
            .map(pathSegment("home")
                .on(action(homeCommandMock)))
            .build();
        // @formatter:on
        assertThat(tree.getRootActionMapper("home").getActionCommand(), equalTo(homeCommandMock));
    }
}