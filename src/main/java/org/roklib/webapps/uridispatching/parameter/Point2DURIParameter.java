package org.roklib.webapps.uridispatching.parameter;


import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
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
    protected ParameterValue<Point2D.Double> consumeParametersImpl(Map<String, String> parameters) {
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

    public int getSingleValueCount() {
        return 2;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, UriPathSegmentActionMapper.ParameterMode parameterMode) {
        ParameterValue<Point2D.Double> pointValue = (ParameterValue<Point2D.Double>) value;
        if (pointValue.hasValue()) {
            if (parameterMode == UriPathSegmentActionMapper.ParameterMode.DIRECTORY_WITH_NAMES) {
                uriTokens.add(xURIParameter.getId());
            }
            uriTokens.add(String.valueOf(pointValue.getValue().getX()));
            if (parameterMode == UriPathSegmentActionMapper.ParameterMode.DIRECTORY_WITH_NAMES) {
                uriTokens.add(yURIParameter.getId());
            }
            uriTokens.add(String.valueOf(pointValue.getValue().getY()));
        }
    }

    @Override
    public String toString() {
        return String.format("{%s: %s, %s}", getClass().getSimpleName(), xURIParameter.getId(), yURIParameter.getId());
    }
}
