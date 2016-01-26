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

import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleLongWithIgnoredTextURIParameter extends SingleLongURIParameter {
    private static final long serialVersionUID = 7990237721421647271L;

    private static final Pattern PATTERN = Pattern.compile("^(\\d+).*?");

    public SingleLongWithIgnoredTextURIParameter(String parameterName, Long defaultValue) {
        super(parameterName, defaultValue);
    }

    public SingleLongWithIgnoredTextURIParameter(String parameterName) {
        super(parameterName);
    }

    @Override
    protected boolean consumeImpl(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.get(getParameterName());
        if (valueList == null || valueList.isEmpty()) {
            return false;
        }
        for (int index = 0; index < valueList.size(); ++index) {
            String value = convertValue(valueList.get(index));
            valueList.set(index, value);
        }

        return super.consumeImpl(parameters);
    }

    private String convertValue(String value) {
        Matcher m = PATTERN.matcher(value);
        if (m.find()) {
            return m.group(1);
        }
        return value;
    }

    @Override
    protected ParameterValue<Long> consumeParametersImpl(String value) {
        return super.consumeParametersImpl(convertValue(value));
    }

}
