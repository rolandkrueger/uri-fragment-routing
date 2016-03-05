package org.roklib.urifragmentrouting;

import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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

    public static class HomeActionCommand implements UriActionCommand {
        @Override
        public void run() {
        }
    }

}