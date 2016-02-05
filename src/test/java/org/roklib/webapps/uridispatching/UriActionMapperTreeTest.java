package org.roklib.webapps.uridispatching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.isA;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.UriActionMapperTreeBuilder;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.create;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeTest {

    @Test
    public void test_create_returns_builder() {
        assertThat(create(), isA(UriActionMapperTreeBuilder.class));
    }

    public static class HomeActionCommand implements UriActionCommand {
        @Override
        public void run() {
        }
    }

}