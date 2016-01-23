/*
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 05.10.2010
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
package org.roklib.webapps.uridispatching.parameters;


import org.roklib.util.helper.CheckForNull;
import org.roklib.webapps.uridispatching.AbstractURIActionCommand;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Point2DURIParameter extends AbstractURIParameter<Point2D.Double> {
    private static final long serialVersionUID = -8452255745085323681L;

    private final List<String> parameterNames;
    private final SingleDoubleURIParameter xURIParameter;
    private final SingleDoubleURIParameter yURIParameter;

    public Point2DURIParameter(String xParamName, String yParamName) {
        CheckForNull.check(xParamName, yParamName);
        parameterNames = new ArrayList<String>(2);
        parameterNames.add(xParamName);
        parameterNames.add(yParamName);
        xURIParameter = new SingleDoubleURIParameter(xParamName);
        yURIParameter = new SingleDoubleURIParameter(yParamName);
    }

    public AbstractURIActionCommand getErrorCommandIfInvalid() {
        return null;
    }

    public void parameterizeURIHandler(AbstractURIPathSegmentActionMapper handler) {
        xURIParameter.setValue(getValue().getX());
        yURIParameter.setValue(getValue().getY());
        xURIParameter.parameterizeURIHandler(handler);
        yURIParameter.parameterizeURIHandler(handler);
    }

    public int getSingleValueCount() {
        return 2;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    protected boolean consumeImpl(Map<String, List<String>> parameters) {
        boolean result = xURIParameter.consume(parameters);
        result &= yURIParameter.consume(parameters);
        if (result) {
            setValue(new Point2D.Double(xURIParameter.getValue(), yURIParameter.getValue()));
        }
        return result;
    }

    @Override
    protected boolean consumeListImpl(String[] values) {
        if (values == null || values.length != 2)
            return false;
        boolean result = xURIParameter.consumeList(Arrays.copyOfRange(values, 0, 1));
        result &= yURIParameter.consumeList(Arrays.copyOfRange(values, 1, 2));
        if (result) {
            setValue(new Point2D.Double(xURIParameter.getValue(), yURIParameter.getValue()));
        }
        return result;
    }

}
