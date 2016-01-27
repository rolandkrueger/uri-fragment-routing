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

public class SingleBooleanURIParameter extends AbstractSingleURIParameter<Boolean> {
    private static final long serialVersionUID = 1181515935142386380L;

    public SingleBooleanURIParameter(String parameterName) {
        super(parameterName);
    }

    public SingleBooleanURIParameter(String parameterName, Boolean defaultValue) {
        super(parameterName);
        setOptional(defaultValue);
    }

    @Override
    protected ParameterValue<Boolean> consumeParametersImpl(String value) {
        if (!(value.equals("1") || value.equals("0") || value.equals("false") || value
                .equals("true"))) {
            return ParameterValue.forError(URIParameterError.CONVERSION_ERROR);
        }

        if (value.equals("1")) {
            return ParameterValue.forValue(true);
        }

        if (value.equals("0")) {
            return ParameterValue.forValue(false);
        }

        return ParameterValue.forValue(Boolean.valueOf(value));
    }

    public URIActionCommand getErrorCommandIfInvalid() {
        return null;
    }
}
