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
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface URIParameter<V> extends Serializable {
    @Deprecated
    boolean consume(Map<String, List<String>> parameters);

    ParameterValue<V> consumeParameters(Map<String, List<String>> parameters);

    boolean consumeList(String[] values);

    @Deprecated
    V getValue();

    @Deprecated
    void setValue(V value);

    @Deprecated
    void clearValue();

    @Deprecated
    URIActionCommand getErrorCommandIfInvalid();

    @Deprecated
    EnumURIParameterErrors getError();

    @Deprecated
    void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler);

    @Deprecated
    void setValueAndParameterizeURIHandler(V value, AbstractURIPathSegmentActionMapper handler);

    @Deprecated
    boolean hasValue();

    void setOptional(boolean optional);

    boolean isOptional();

    int getSingleValueCount();

    List<String> getParameterNames();
}