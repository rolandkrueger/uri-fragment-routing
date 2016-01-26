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


import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSingleURIParameter<V extends Serializable> extends AbstractURIParameter<V> {
    private static final long serialVersionUID = -4048110873045678896L;

    private final List<String> parameterName;

    public AbstractSingleURIParameter(String parameterName, boolean optional) {
        this(parameterName);
        setOptional(optional);
    }

    public AbstractSingleURIParameter(String parameterName) {
        Preconditions.checkNotNull(parameterName);

        this.parameterName = new LinkedList<String>();
        this.parameterName.add(parameterName);
    }

    protected String getParameterName() {
        return parameterName.get(0);
    }

    public int getSingleValueCount() {
        return 1;
    }

    public List<String> getParameterNames() {
        return parameterName;
    }

    public void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler) {
        if (value != null) {
            handler.addActionArgument(parameterName.get(0), value);
        }
    }

    public final ParameterValue<V> consumeParameters(Map<String, List<String>> parameters) {
        List<String> valueList = parameters.get(getParameterName());
        if (!(valueList == null || valueList.isEmpty())) {
            return consumeParametersImpl(valueList.get(0));
        }
        return null;
    }

    protected abstract ParameterValue<V> consumeParametersImpl(String value);

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + ": " + parameterName.get(0) + "}";
    }
}
