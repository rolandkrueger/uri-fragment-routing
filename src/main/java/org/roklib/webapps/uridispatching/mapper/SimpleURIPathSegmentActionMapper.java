/*
 * Copyright (C) 2007 - 2010
 * Roland Krueger Created on 10.03.2010
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

import org.roklib.webapps.uridispatching.AbstractURIActionCommand;

import java.util.List;
import java.util.Map;

/**
 * A simple URI action mapper that directly returns a predefined action command when the URI interpretation process
 * encounters this mapper. By that, {@link SimpleURIPathSegmentActionMapper}s always represent the last token of an
 * interpreted URI as they cannot dispatch to any sub-mappers.
 *
 * @author Roland Krüger
 */
public class SimpleURIPathSegmentActionMapper extends org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper {
    private static final long serialVersionUID = 8203362201388037000L;

    /**
     * Create a new {@link SimpleURIPathSegmentActionMapper} with the specified action name and action command.
     *
     * @param segmentName the name of the URI path segment this mapper is responsible for
     */
    public SimpleURIPathSegmentActionMapper(String segmentName) {
        super(segmentName);
    }

    /**
     * Directly returns the URI action command passed in through the constructor. All method arguments are ignored.
     */
    @Override
    protected AbstractURIActionCommand handleURIImpl(List<String> uriTokens, Map<String, List<String>> parameters,
                                                     ParameterMode parameterMode) {
        return getActionCommand();
    }
}
