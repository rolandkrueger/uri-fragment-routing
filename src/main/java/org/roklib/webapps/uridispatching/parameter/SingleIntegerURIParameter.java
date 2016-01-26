/*
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 02.03.2010
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


import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;

public class SingleIntegerURIParameter extends AbstractSingleURIParameter<Integer> {
    private static final long serialVersionUID = - 8886216456838021135L;

    public SingleIntegerURIParameter(String parameterName) {
        super(parameterName);
    }

    public SingleIntegerURIParameter(String parameterName, Integer defaultValue) {
        super(parameterName);
        setDefaultValue(defaultValue);
    }

    protected boolean consumeImpl(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.remove(getParameterName());
        return ! (valueList == null || valueList.isEmpty()) && consumeValue(valueList.get(0));
    }

    @Override
    protected boolean consumeListImpl(String[] values) {
        return ! (values == null || values.length == 0) && consumeValue(values[0]);
    }

    private boolean consumeValue(String stringValue) {
        try {
            setValue(Integer.valueOf(stringValue));
            return true;
        } catch (NumberFormatException nfExc) {
            error = EnumURIParameterErrors.CONVERSION_ERROR;
            return false;
        }
    }

    @Override
    public ParameterValue<Integer> consumeParametersImpl(String value) {
        try {
            return new ParameterValue<>(Integer.valueOf(value));
        } catch (NumberFormatException nfExc) {
            // TODO: handle error
            error = EnumURIParameterErrors.CONVERSION_ERROR;
        }
        return null;
    }

    public URIActionCommand getErrorCommandIfInvalid() {
        return null;
    }
}