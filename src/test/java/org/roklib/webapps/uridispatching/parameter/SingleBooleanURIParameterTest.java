/*
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 07.03.2010
 *
 * Author: Roland Krueger (www.rolandkrueger.info)
 *
 * This file is part of RoKlib.
 *
 * This library is free software; you can redistribute it and/or
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SingleBooleanURIParameterTest extends AbstractSingleURIParameterTest<Boolean> {
    @Override
    public AbstractSingleURIParameter<Boolean> getTestSingleURIParameter(String parameterName) {
        return new SingleBooleanURIParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "true";
    }

    @Override
    public Boolean getTestValue() {
        return Boolean.TRUE;
    }

    @Override
    public void testConsume() {
        super.testConsume();
        AbstractSingleURIParameter<Boolean> testObj = getTestSingleURIParameter("test");

        Map<String, List<String>> parameters = new HashMap<String, List<String>>();

        parameters.put("test", Arrays.asList("false"));
        testObj.consume(parameters);
        assertEquals(EnumURIParameterErrors.NO_ERROR, testObj.getError());
        assertEquals(Boolean.FALSE, testObj.getValue());

        parameters.put("test", Arrays.asList("0"));
        testObj.consume(parameters);
        assertEquals(EnumURIParameterErrors.NO_ERROR, testObj.getError());
        assertEquals(Boolean.FALSE, testObj.getValue());

        parameters.put("test", Arrays.asList("1"));
        testObj.consume(parameters);
        assertEquals(EnumURIParameterErrors.NO_ERROR, testObj.getError());
        assertEquals(Boolean.TRUE, testObj.getValue());
    }
}
