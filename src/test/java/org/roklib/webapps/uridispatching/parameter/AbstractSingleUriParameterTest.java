package org.roklib.webapps.uridispatching.parameter;

import org.junit.Before;
import org.roklib.webapps.uridispatching.TUriActionCommand;
import org.roklib.webapps.uridispatching.mapper.TUriPathSegmentActionMapper;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class AbstractSingleUriParameterTest<V extends Serializable> extends AbstractUriParameterTest<V> {
    private AbstractSingleUriParameter<V> testSingleURIParameter;

    public abstract AbstractSingleUriParameter<V> getTestSingleURIParameter(String parameterName);

    public abstract String getTestValueAsString();

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

    @SuppressWarnings("serial")
    private static class T2UriPathSegmentActionMapper<V extends Serializable> extends TUriPathSegmentActionMapper {
        String mExpectedParameterName;
        V mExpectedValue;

        public T2UriPathSegmentActionMapper(String expectedParameterName, V expectedValue) {
            super("", TUriActionCommand.class);
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
