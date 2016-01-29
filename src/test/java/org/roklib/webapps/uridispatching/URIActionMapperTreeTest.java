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
    private Class<? extends URIActionCommand> homeCommandMock;

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