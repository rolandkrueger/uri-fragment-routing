package org.roklib.webapps.uridispatching.parameter;

import org.hamcrest.core.IsCollectionContaining;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author rkrueger
 */
public class StringListUriParameterTest extends AbstractUriParameterTest<List<String>> {

    @Override
    public AbstractUriParameter<List<String>> getTestURIParameter() {
        return new StringListUriParameter("list");
    }

    @Override
    public List<String> getTestValue() {
        return Arrays.asList("a", "b", "c");
    }

    @Override
    public List<String> getDefaultValue() {
        return Arrays.asList("default", "value");
    }

    @Override
    public void testGetSingleValueCount() {
        assertThat(getTestURIParameter().getSingleValueCount(), is(equalTo(1)));
    }

    @Override
    public void testGetParameterNames() {
        assertThat(getTestURIParameter().getParameterNames(), IsCollectionContaining.hasItems("list"));
    }
}