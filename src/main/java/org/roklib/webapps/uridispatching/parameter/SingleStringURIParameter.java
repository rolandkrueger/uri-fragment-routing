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


import org.roklib.webapps.uridispatching.URIActionCommand;

import java.util.List;
import java.util.Map;

public class SingleStringURIParameter extends AbstractSingleURIParameter<String> {
    private static final long serialVersionUID = -9010093565464929620L;

    public SingleStringURIParameter(String parameterName) {
        super(parameterName);
    }

    public SingleStringURIParameter(String parameterName, String defaultString) {
        super(parameterName);
        setDefaultValue(defaultString);
    }

    protected boolean consumeImpl(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.remove(getParameterName());
        if (valueList != null) {
            setValue(valueList.get(0));
            return true;
        }
        return false;
    }

    @Override
    protected boolean consumeListImpl(String[] values) {
        if (values == null || values.length == 0)
            return false;
        setValue(values[0]);
        return true;
    }

    public URIActionCommand getErrorCommandIfInvalid() {
        return null;
    }
}
