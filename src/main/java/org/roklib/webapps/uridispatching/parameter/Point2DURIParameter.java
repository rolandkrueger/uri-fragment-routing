package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Point2DUriParameter extends AbstractUriParameter<Point2D.Double> {
    private static final long serialVersionUID = - 8452255745085323681L;

    private final List<String> parameterNames;
    private final SingleDoubleUriParameter xURIParameter;
    private final SingleDoubleUriParameter yURIParameter;

    public Point2DUriParameter(String name, String xParamName, String yParamName) {
        super(name);
        Preconditions.checkNotNull(xParamName);
        Preconditions.checkNotNull(yParamName);
        parameterNames = new ArrayList<>(2);
        parameterNames.add(xParamName);
        parameterNames.add(yParamName);
        xURIParameter = new SingleDoubleUriParameter(xParamName);
        yURIParameter = new SingleDoubleUriParameter(yParamName);
    }

    @Override
    protected ParameterValue<Point2D.Double> consumeParametersImpl(Map<String, List<String>> parameters) {
        ParameterValue<Double> xValue = xURIParameter.consumeParameters(parameters);
        ParameterValue<Double> yValue = yURIParameter.consumeParameters(parameters);

        if (xValue.hasError()) {
            return ParameterValue.forError(xValue.getError());
        }

        if (yValue.hasError()) {
            return ParameterValue.forError(yValue.getError());
        }

        return ParameterValue.forValue(new Point2D.Double(xValue.getValue(), yValue.getValue()));
    }

    public void parameterizeURIHandler(AbstractUriPathSegmentActionMapper handler) {
        //xURIParameter.setValue(getValue().getX());
        //yURIParameter.setValue(getValue().getY());
        xURIParameter.parameterizeURIHandler(handler);
        yURIParameter.parameterizeURIHandler(handler);
    }

    public int getSingleValueCount() {
        return 2;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }
}
