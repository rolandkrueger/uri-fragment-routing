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


import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;

import java.util.List;
import java.util.Map;

public abstract class AbstractURIParameter<V> implements URIParameter<V> {
    private static final long serialVersionUID = 2304452724109724238L;

    protected EnumURIParameterErrors error;
    protected V value;
    private V defaultValue = null;
    private boolean optional = false;

    protected abstract boolean consumeImpl(Map<String, List<String>> parameters);

    public AbstractURIParameter() {
        error = EnumURIParameterErrors.NO_ERROR;
    }

    public final boolean consume(Map<String, List<String>> parameters) {
        error = EnumURIParameterErrors.NO_ERROR;
        boolean result = consumeImpl(parameters);
        postConsume();
        return result;
    }

    private void postConsume() {
        if (!hasValue()) {
            value = defaultValue;
        }
        if (!hasValue() && !optional && error == EnumURIParameterErrors.NO_ERROR) {
            error = EnumURIParameterErrors.PARAMETER_NOT_FOUND;
        }
    }

    public void setDefaultValue(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected void setError(EnumURIParameterErrors error) {
        this.error = error;
    }

    public EnumURIParameterErrors getError() {
        return error;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setValueAndParameterizeURIHandler(V value, AbstractURIPathSegmentActionMapper handler) {
        setValue(value);
        parameterizeURIHandler(handler);
    }

    public void clearValue() {
        error = EnumURIParameterErrors.NO_ERROR;
        value = null;
    }

    public boolean hasValue() {
        return error == EnumURIParameterErrors.NO_ERROR && value != null;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }
}
