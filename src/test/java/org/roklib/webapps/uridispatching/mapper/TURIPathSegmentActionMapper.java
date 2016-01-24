/*
 * Copyright (C) 2007 - 2010 Roland Krueger 
 * Created on 17.02.2010
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
package org.roklib.webapps.uridispatching.mapper;


import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.TURIActionCommand;
import org.roklib.webapps.uridispatching.parameters.URIParameter;

import java.util.List;
import java.util.Map;

public class TURIPathSegmentActionMapper extends org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper {
    private static final long serialVersionUID = 6202866717473440168L;

    private TURIActionCommand command;

    public TURIPathSegmentActionMapper(String actionName, TURIActionCommand command) {
        super(actionName);
        this.command = command;
    }

    @Override
    protected URIActionCommand handleURIImpl(List<String> uriTokens, Map<String, List<String>> parameters,
                                             ParameterMode parameterMode) {
        return command;
    }

    public void registerURLParameterForTest(URIParameter<?> parameter, boolean optional) {
        parameter.setOptional(optional);
        registerURIParameter(parameter);
    }

    public boolean haveRegisteredURIParametersErrors() {
        return super.haveRegisteredURIParametersErrors();
    }
}
