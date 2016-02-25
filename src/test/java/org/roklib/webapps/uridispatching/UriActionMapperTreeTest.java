package org.roklib.webapps.uridispatching;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.isA;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.UriActionMapperTreeBuilder;
import static org.roklib.webapps.uridispatching.UriActionMapperTree.create;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeTest {

    private UriActionMapperTree mapperTree;

    @After
    public void printMapperTree() {
        if (mapperTree != null) {
            System.out.println("Testing with following mapper tree:\n");
            mapperTree.print(System.out);
            System.out.println("----------------------------------------");
        }
    }

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