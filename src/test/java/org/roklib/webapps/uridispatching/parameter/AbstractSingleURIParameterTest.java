/*
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 07.03.2010
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
            super("", new TURIActionCommand());
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
