package org.roklib.urifragmentrouting.parameter;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractUriParameterTest<V> {
    private AbstractUriParameter<V> parameter;

    public abstract AbstractUriParameter<V> getTestURIParameter();

    public abstract V getTestValue();

    public abstract void assertUriTokenListForTestValueWithoutDirectoryNames(List<String> uriTokens);

    public abstract void assertUriTokenListForTestValueWithDirectoryNames(List<String> uriTokens);

    public abstract V getDefaultValue();

    public abstract Map<String, String> getParameterValuesToConsume();

    public abstract void assertConsumedParameterValue(ParameterValue<V> parameterValue);

    @Before
    public void setUp() {
        parameter = getTestURIParameter();
    }

    @Test
    public abstract void testGetSingleValueCount();

    @Test
    public abstract void testGetParameterNames();

    @Test
    public void testSetOptional() {
        assertFalse(parameter.isOptional());
        parameter.setOptional(getDefaultValue());
        assertTrue(parameter.isOptional());
    }

    @Test
    public void test_consume() {
        Map<String, String> parametersMap = getParameterValuesToConsume();
        final ParameterValue<V> value = parameter.consumeParameters(parametersMap);
        assertConsumedParameterValue(value);
    }

    @Test
    public void test_toUriTokenList_without_directory_names() {
        List<String> uriTokens = new LinkedList<>();
        parameter.toUriTokenList(ParameterValue.forValue(getTestValue()), uriTokens, ParameterMode.DIRECTORY);
        assertUriTokenListForTestValueWithoutDirectoryNames(uriTokens);
    }

    @Test
    public void test_toUriTokenList_with_directory_names() {
        List<String> uriTokens = new LinkedList<>();
        parameter.toUriTokenList(ParameterValue.forValue(getTestValue()), uriTokens, ParameterMode.DIRECTORY_WITH_NAMES);
        assertUriTokenListForTestValueWithDirectoryNames(uriTokens);
    }
}