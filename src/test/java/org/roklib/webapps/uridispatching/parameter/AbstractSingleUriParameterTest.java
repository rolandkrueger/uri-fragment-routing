package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

public abstract class AbstractSingleUriParameterTest<V extends Serializable> extends AbstractUriParameterTest<V> {
    private AbstractSingleUriParameter<V> testSingleURIParameter;

    public abstract AbstractSingleUriParameter<V> getTestSingleURIParameter(String parameterName);

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
}
