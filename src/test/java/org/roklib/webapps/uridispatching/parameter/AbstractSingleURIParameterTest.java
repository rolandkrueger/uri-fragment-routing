package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;
import org.roklib.webapps.uridispatching.TURIActionCommand;
import org.roklib.webapps.uridispatching.mapper.TURIPathSegmentActionMapper;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class AbstractSingleURIParameterTest<V extends Serializable> extends AbstractURIParameterTest<V> {
    private AbstractSingleURIParameter<V> testSingleURIParameter;

    public abstract AbstractSingleURIParameter<V> getTestSingleURIParameter(String parameterName);

    public abstract String getTestValueAsString();

    @Before
    public void setUp() {
        super.setUp();
        testSingleURIParameter = getTestSingleURIParameter("test");
    }

    public AbstractURIParameter<V> getTestURIParameter() {
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

    @SuppressWarnings("serial")
    private static class T2URIPathSegmentActionMapper<V extends Serializable> extends TURIPathSegmentActionMapper {
        String mExpectedParameterName;
        V mExpectedValue;

        public T2URIPathSegmentActionMapper(String expectedParameterName, V expectedValue) {
            super("", TURIActionCommand.class);
            mExpectedParameterName = expectedParameterName;
            mExpectedValue = expectedValue;
        }

        @Override
        public void addActionArgument(String argumentName, Serializable... argumentValues) {
            super.addActionArgument(argumentName, argumentValues);
            assertEquals(1, argumentValues);
            assertEquals(mExpectedParameterName, argumentName);
            if (mExpectedValue == null)
                assertNull(argumentValues[0]);
            else
                assertEquals(mExpectedValue, argumentValues[0]);
        }
    }
}
