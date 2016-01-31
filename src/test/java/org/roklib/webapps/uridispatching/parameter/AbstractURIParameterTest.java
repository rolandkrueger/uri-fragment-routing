package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractURIParameterTest<V extends Serializable> {
    private AbstractURIParameter<V> testObj;

    public abstract AbstractURIParameter<V> getTestURIParameter();

    public abstract V getTestValue();

    public abstract V getDefaultValue();

    @Test
    public abstract void testGetSingleValueCount();

    @Test
    public abstract void testGetParameterNames();

    @Before
    public void setUp() {
        testObj = getTestURIParameter();
    }


    @Test
    public void testSetOptional() {
        testObj.setOptional(getDefaultValue());
        assertTrue(testObj.isOptional());
        testObj.setOptional(getDefaultValue());
        assertFalse(testObj.isOptional());
    }

    @SuppressWarnings("serial")
    private static class TURLParameter extends AbstractURIParameter<String> {
        boolean parameterized = false;

        public TURLParameter(String id) {
            super(id);
        }

        public boolean parameterized() {
            return parameterized;
        }

        @Override
        protected ParameterValue<String> consumeParametersImpl(Map<String, List<String>> parameters) {
            return null;
        }

        public URIActionCommand getErrorCommandIfInvalid() {
            return null;
        }

        public void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler) {
            parameterized = true;
        }

        public int getSingleValueCount() {
            return 1;
        }

        public List<String> getParameterNames() {
            return null;
        }
    }
}