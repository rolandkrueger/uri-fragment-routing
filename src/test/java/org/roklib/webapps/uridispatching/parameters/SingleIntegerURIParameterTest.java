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
package org.roklib.webapps.uridispatching.parameters;

public class SingleIntegerURIParameterTest extends AbstractSingleURIParameterTest<Integer> {
    @Override
    public AbstractSingleURIParameter<Integer> getTestSingleURIParameter(String parameterName) {
        SingleIntegerURIParameter result = new SingleIntegerURIParameter("test");
        result.setOptional(false);
        return result;
    }

    @Override
    public String getTestValueAsString() {
        return "123";
    }

    @Override
    public Integer getTestValue() {
        return 123;
    }
}
