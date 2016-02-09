package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class Point2DUriParameterTest extends AbstractUriParameterTest<Double> {
    @Override
    public AbstractUriParameter<Double> getTestURIParameter() {
        return new Point2DUriParameter("point", "testX", "testY");
    }

    @Override
    public Double getTestValue() {
        return new Double(1.0, 2.0);
    }

    @Override
    public void assertUriTokenListForTestValueWithoutDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains("1.0", "2.0"));
    }

    @Override
    public void assertUriTokenListForTestValueWithDirectoryNames(List<String> uriTokens) {
        assertThat(uriTokens, contains("testX", "1.0", "testY", "2.0"));
    }

    @Override
    public Double getDefaultValue() {
        return new Double(17.0, 23.0);
    }

    @Override
    public Map<String, String> getParameterValuesToConsume() {
        Map<String, String> map = new HashMap<>();
        map.put("testX", "1.0");
        map.put("testY", "2.0");
        return map;
    }

    @Override
    public void assertConsumedParameterValue(ParameterValue<Double> parameterValue) {
        assertThat(parameterValue.hasValue(), is(true));
        assertThat(parameterValue.getValue().getX(), is(1.0));
        assertThat(parameterValue.getValue().getY(), is(2.0));
    }

    @Override
    public void testGetSingleValueCount() {
        assertEquals(2, getTestURIParameter().getSingleValueCount());
    }

    @Override
    public void testGetParameterNames() {
        assertEquals(2, getTestURIParameter().getParameterNames().size());
        assertEquals("testX", getTestURIParameter().getParameterNames().get(0));
        assertEquals("testY", getTestURIParameter().getParameterNames().get(1));
    }
}
