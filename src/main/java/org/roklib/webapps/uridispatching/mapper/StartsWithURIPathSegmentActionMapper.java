/*
 * Copyright (C) 2007 Roland Krueger Created on 22.09.2012
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

/**
 * <p> URI action handler for matching all URI tokens which start with some particular character string. As this action
 * handler class is a particularly configured {@link RegexURIPathSegmentActionMapper}, all of the description of {@link
 * RegexURIPathSegmentActionMapper} also applies to this class. </p> <p> This action handler is initialized with some
 * prefix string which must not be all whitespaces or the empty string. By default, there is one capturing group in the
 * regular expression that underlies this class. This group captures any substring that comes after the given prefix
 * string in the currently evaluated URI token. </p>
 *
 * @author Roland Krüger
 * @see RegexURIPathSegmentActionMapper
 * @since 1.1.0
 */
public class StartsWithURIPathSegmentActionMapper extends RegexURIPathSegmentActionMapper {
    private static final long serialVersionUID = -8311620063509162064L;

    /**
     * Creates a new {@link StartsWithURIPathSegmentActionMapper} with the given prefix string.
     *
     * @param prefix prefix string to be used for interpreting URI tokens.
     * @throws IllegalArgumentException if the prefix is the empty string or all whitespaces
     */
    public StartsWithURIPathSegmentActionMapper(String prefix) {
        super(prefix + "(.*)");
        if ("".equals(prefix.trim())) {
            throw new IllegalArgumentException("prefix must not be the empty string or all whitespaces");
        }
    }
}
