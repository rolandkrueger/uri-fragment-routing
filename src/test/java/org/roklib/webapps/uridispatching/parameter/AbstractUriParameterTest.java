package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractUriParameterTest<V> {
    private AbstractUriParameter<V> testObj;

    public abstract AbstractUriParameter<V> getTestURIParameter();

    public abstract V getTestValue();

    public abstract V getDefaultValue();

    @Before
    public void setUp() {
        testObj = getTestURIParameter();
    }

    @Test
    public abstract void testGetSingleValueCount();

    @Test
    public abstract void testGetParameterNames();

    @Test
    public void testSetOptional() {
        assertFalse(testObj.isOptional());
        testObj.setOptional(getDefaultValue());
        assertTrue(testObj.isOptional());
    }
}