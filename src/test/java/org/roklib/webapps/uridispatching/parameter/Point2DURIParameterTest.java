package org.roklib.webapps.uridispatching.parameter;

import java.awt.geom.Point2D.Double;

import static org.junit.Assert.assertEquals;

public class Point2DUriParameterTest extends AbstractURIParameterTest<Double> {
    @Override
    public AbstractUriParameter<Double> getTestURIParameter() {
        return new Point2DUriParameter("point", "testX", "testY");
    }

    @Override
    public Double getTestValue() {
        return new Double(1.0, 2.0);
    }

    @Override
    public Double getDefaultValue() {
        return new Double(17.0, 23.0);
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
