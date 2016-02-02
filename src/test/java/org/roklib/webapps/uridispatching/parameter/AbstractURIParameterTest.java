package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractURIParameterTest<V> {
    private AbstractURIParameter<V> testObj;

    public abstract AbstractURIParameter<V> getTestURIParameter();

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