package org.roklib.urifragmentrouting.parameter;


import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This URI parameter is used to put two-dimensional coordinates as a parameter pair into a URI fragment. A {@link
 * Point2DUriParameter} consists of two values which are of domain type Double. These two values represent an x- and a
 * y-coordinate, respectively. The domain type of the coordinate pair itself is {@link java.awt.geom.Point2D.Double}.
 * This parameter class is useful if you want to put geographical coordinates into a URI fragment, for example.
 * <p>
 * When creating a new {@link Point2DUriParameter} you have to provide the parameter's id and two parameter names: one
 * for the x-coordinate and one for the y-coordinate. As an example, consider the following parameter instance:
 * <p>
 * {@code Point2DUriParameter coordinates = new Point2DUriParameter("coords", "lat", "lon");}
 * <p>
 * This parameter could appear in a URI fragment as follows:
 * <p>
 * <tt>http://www.example.com/venueFinder#!venues/findNear/lat/49.487459/lon/8.466039</tt>
 * <p>
 * Note that the parameter's id does not appear in the URI fragment. It is needed to identify the parameter in a {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree}.
 */
public class Point2DUriParameter extends AbstractUriParameter<Point2D.Double> {
    private static final long serialVersionUID = -8452255745085323681L;
    private final List<String> parameterNames;
    private final SingleDoubleUriParameter xURIParameter;
    private final SingleDoubleUriParameter yURIParameter;

    /**
     * Create a new parameter for coordinates with the given id and parameter names.
     *
     * @param id         id of the parameter which is used to identify it in a URI action mapper tree
     * @param xParamName name for the x-coordinate parameter
     * @param yParamName name for the y-coordinate parameter
     */
    public Point2DUriParameter(final String id, final String xParamName, final String yParamName) {
        super(id);
        Preconditions.checkNotNull(xParamName);
        Preconditions.checkNotNull(yParamName);
        parameterNames = new ArrayList<>(2);
        parameterNames.add(xParamName);
        parameterNames.add(yParamName);
        xURIParameter = new SingleDoubleUriParameter(xParamName);
        yURIParameter = new SingleDoubleUriParameter(yParamName);
    }

    @Override
    protected ParameterValue<Point2D.Double> consumeParametersImpl(final Map<String, String> parameters) {
        final ParameterValue<Double> xValue = xURIParameter.consumeParameters(parameters);
        final ParameterValue<Double> yValue = yURIParameter.consumeParameters(parameters);

        if (isOptional() && xValue.getError() == UriParameterError.PARAMETER_NOT_FOUND && yValue.getError() == UriParameterError.PARAMETER_NOT_FOUND) {
            return null;
        }

        if (xValue.hasError()) {
            return ParameterValue.forError(xValue.getError());
        }

        if (yValue.hasError()) {
            return ParameterValue.forError(yValue.getError());
        }

        return ParameterValue.forValue(new Point2D.Double(xValue.getValue(), yValue.getValue()));
    }

    /**
     * {@inheritDoc}
     *
     * @return the count of the individual values for this parameter which is 2 (x- and y-coordinate)
     */
    @Override
    public int getSingleValueCount() {
        return 2;
    }

    @Override
    public List<String> getParameterNames() {
        return parameterNames;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void toUriTokenList(final ParameterValue<?> value, final List<String> uriTokens, final ParameterMode parameterMode) {
        final ParameterValue<Point2D.Double> pointValue = (ParameterValue<Point2D.Double>) value;
        if (pointValue.hasValue()) {
            if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                uriTokens.add(xURIParameter.getId());
            }
            uriTokens.add(String.valueOf(pointValue.getValue().getX()));
            if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                uriTokens.add(yURIParameter.getId());
            }
            uriTokens.add(String.valueOf(pointValue.getValue().getY()));
        }
    }

    @Override
    public String toString() {
        return String.format("{%s: id='%s', xParam='%s', yParam='%s'}", getClass().getSimpleName(), getId(), xURIParameter.getId(), yURIParameter.getId());
    }
}
