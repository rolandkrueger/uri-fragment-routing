/*
 * Copyright (C) 2007 Roland Krueger
 * Created on 04.11.2010
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


import org.junit.Test;

public class SingleLongWithIgnoredTextURIParameterTest extends AbstractSingleURIParameterTest<Long> {
    @Override
    public AbstractSingleURIParameter<Long> getTestSingleURIParameter(String parameterName) {
        return new SingleLongWithIgnoredTextURIParameter("test");
    }

    @Override
    public String getTestValueAsString() {
        return "1234-test";
    }

    @Override
    public Long getTestValue() {
        return 1234L;
    }

    @Test
    public void testConsume2() {
        testConsume("1234");
        testConsume("1234-");
        testConsume("1234text");
    }

    @Test
    public void testConsumeList2() {
        testConsumeList("1234");
        testConsumeList("1234-");
        testConsumeList("1234text");
    }
}
