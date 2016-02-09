package org.roklib.webapps.uridispatching.parameter;

import org.hamcrest.core.IsCollectionContaining;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class StringListUriParameterTest extends AbstractUriParameterTest<List<String>> {

    @Override
    public AbstractUriParameter<List<String>> getTestURIParameter() {
        return new StringListUriParameter("list");
    }

    @Override
    public List<String> getTestValue() {
        return Arrays.asList("a;b", "c/d", "e");
    }

    @Override
    public void assertUriTokenListForTestValueWithoutDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains("a%3Bb;c%2Fd;e"));
    }

    @Override
    public void assertUriTokenListForTestValueWithDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains("list", "a%3Bb;c%2Fd;e"));
    }

    @Override
    public List<String> getDefaultValue() {
        return Arrays.asList("default", "value");
    }

    @Override
    public Map<String, String> getParameterValuesToConsume() {
        Map<String, String> map = new HashMap<>();
        map.put("list", "a%3Bb;c%2Fd;e");
        return map;
    }

    @Override
    public void assertConsumedParameterValue(ParameterValue<List<String>> parameterValue) {
        assertThat(parameterValue.hasValue(), is(true));
        assertThat(parameterValue.getValue(), contains("a;b", "c/d", "e"));
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