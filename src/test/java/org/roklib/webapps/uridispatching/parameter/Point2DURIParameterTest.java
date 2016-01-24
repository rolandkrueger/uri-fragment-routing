/*
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 31.10.2010
 *
 * Author: Roland Krueger (www.rolandkrueger.info)
 *
 * This file is part of RoKlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.roklib.webapps.uridispatching.parameter;

import java.awt.geom.Point2D.Double;

import static org.junit.Assert.assertEquals;

public class Point2DURIParameterTest extends AbstractURIParameterTest<Double> {
    @Override
    public AbstractURIParameter<Double> getTestURIParameter() {
        return new Point2DURIParameter("testX", "testY");
    }

    @Override
    public Double getTestValue() {
        return new Double(1.0, 2.0);
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
