package org.roklib.urifragmentrouting.parameter;

import org.junit.Before;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public abstract class AbstractSingleUriParameterTest<V> extends AbstractUriParameterTest<V> {
    private AbstractSingleUriParameter<V> testSingleURIParameter;

    public abstract AbstractSingleUriParameter<V> getTestSingleURIParameter(String parameterName);

    public abstract ParameterValueConverter<V> getTypeConverter();

    @Before
    public void setUp() {
        super.setUp();
        testSingleURIParameter = getTestSingleURIParameter("test");
    }

    public AbstractUriParameter<V> getTestURIParameter() {
        return getTestSingleURIParameter("test");
    }

    @Override
    public void testGetSingleValueCount() {
        assertEquals(1, testSingleURIParameter.getSingleValueCount());
    }

    @Override
    public void testGetParameterNames() {
        assertEquals(1, testSingleURIParameter.getParameterNames().size());
    }

    @Override
    public Map<String, String> getParameterValuesToConsume() {
        Map<String, String> map = new HashMap<>();
        map.put("test", getTypeConverter().convertToString(getTestValue()));
        return map;
    }

    @Override
    public void assertUriTokenListForTestValueWithoutDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains(getTypeConverter().convertToString(getTestValue())));
    }

    @Override
    public void assertUriTokenListForTestValueWithDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains("test", getTypeConverter().convertToString(getTestValue())));
    }

    @Override
    public void assertConsumedParameterValue(ParameterValue<V> parameterValue) {
        assertThat(parameterValue.hasValue(), is(true));
        assertThat(parameterValue.getValue(), is(equalTo(getTestValue())));
    }
}
