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


import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;

public abstract class AbstractURIParameter<V> implements URIParameter<V> {
    private static final long serialVersionUID = 2304452724109724238L;

    protected URIParameterError error;
    protected V value;
    private V defaultValue = null;
    private boolean optional = false;

    protected abstract boolean consumeImpl(Map<String, List<String>> parameters);

    public AbstractURIParameter() {
        error = URIParameterError.NO_ERROR;
    }

    public final boolean consume(Map<String, List<String>> parameters) {
        error = URIParameterError.NO_ERROR;
        return consumeImpl(parameters);
    }

    public ParameterValue<V> consumeParameters(Map<String, List<String>> parameters){
        error = URIParameterError.NO_ERROR;
        final ParameterValue<V> result = consumeParametersImpl(parameters);
        return postConsume(result);
    }

    protected abstract ParameterValue<V> consumeParametersImpl(Map<String, List<String>> parameters);

    private ParameterValue<V> postConsume(ParameterValue<V> value) {
        if (value == null && defaultValue != null && optional) {
            return ParameterValue.forDefaultValue(defaultValue);
        }
        if (value == null) {
            return ParameterValue.forError(URIParameterError.PARAMETER_NOT_FOUND);
        }
        return value;
    }

    protected void setError(URIParameterError error) {
        this.error = error;
    }

    public URIParameterError getError() {
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

    public boolean hasValue() {
        return error == URIParameterError.NO_ERROR && value != null;
    }

    public void setOptional(V defaultValue) {
        Preconditions.checkNotNull(defaultValue);
        this.optional = true;
        this.defaultValue = defaultValue;
    }

    public boolean isOptional() {
        return optional;
    }
}
