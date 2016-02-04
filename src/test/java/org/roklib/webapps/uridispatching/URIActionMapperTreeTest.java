package org.roklib.webapps.uridispatching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.*;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeTest {

    @Test
    public void test_create_returns_builder() {
        assertThat(create(), isA(UriActionMapperTreeBuilder.class));
    }

    @Test
    public void test_getRootActionMapper() {
        // @formatter:off
        final UriActionMapperTree tree = create()
            .map(pathSegment("home")
                .on(action(HomeActionCommand.class)))
            .build();
        // @formatter:on
        assertThat(tree.getRootActionMapper("home").getActionCommand(), is(equalTo((HomeActionCommand.class))));
    }

    public static class HomeActionCommand implements UriActionCommand {
        @Override
        public void execute() {
        }
    }

}