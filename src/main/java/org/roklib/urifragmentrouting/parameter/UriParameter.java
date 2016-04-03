package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A URI parameter can be registered on an {@link org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper}
 * to capture individual values contained in a URI fragment. Consider, for example, the following URL:
 * <p>
 * <tt>http://www.example.com/app#!show/details/id/42</tt>
 * <p>
 * Here, the two path segments <tt>show</tt> and <tt>details</tt> are managed by subclasses of {@link
 * org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper}. The latter action mapper has one registered
 * parameter with name <tt>id</tt> and a current value of <tt>42</tt>. You can register any number of parameters on any
 * action mapper, i. e. even action mappers in the middle of a URI fragment can have registered parameters. After a URI
 * fragment visited by a user has been interpreted, all captured parameter values for this fragment can be retrieved
 * from an object of type {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues}.
 * <p>
 * Each URI parameter has its own particular domain type specified with class type <tt>V</tt>. The textual parameter
 * values found in a URI fragment will be converted into these domain types using a {@link ParameterValueConverter} when
 * a URI fragment is interpreted. By that, the application code does not have to bother with this conversion task but
 * instead obtains parameter values of the correct type.
 * <p>
 * URI parameter values can be added to a URI fragment in three different ways, governed by the {@link ParameterMode}.
 * Using the example URL from above, these variants are as follows:
 * <p>
 * <ul> <li>Query mode: <tt>http://www.example.com/app#!show/details?id=42</tt></li> <li>Directory mode with directory
 * names: <tt>http://www.example.com/app#!show/details/id/42</tt></li> <li>Directory mode without directory names:
 * <tt>http://www.example.com/app#!show/details/42</tt></li> </ul>
 * <p>
 * A URI parameter is uniquely identified by its id as specified by method {@link #getId()}. This id is used to retrieve
 * an instance of a {@link ParameterValue} object from an instance of {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues}.
 * Besides this id, a URI parameter can have one or more parameter names which identify the individual parameter values
 * for multi-valued parameters (see next paragraph).
 * <p>
 * A URI parameter can be single-valued or multi-valued. A single-valued URI parameter is responsible for exactly one
 * value in a URI fragment. For example, the <tt>id</tt> parameter from the example above is such a single-valued
 * parameter. The parameter name for this parameter is <tt>id</tt>. In fact, parameter id and parameter name coincide
 * for single-valued parameters. A multi-valued parameter manages more than on parameter value. An example for such a
 * type of parameter is {@link Point2DUriParameter} which is used to put a two-dimensional coordinate (such as a
 * latitude and longitude value) into a URI fragment. A {@link Point2DUriParameter} has one parameter id and two
 * parameter names which can be custom defined.
 *
 * @param <V> the domain type of the parameter value
 * @see ParameterValueConverter
 * @see ParameterMode
 * @see org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues
 * @see org.roklib.urifragmentrouting.annotation.AllCapturedParameters
 * @see org.roklib.urifragmentrouting.annotation.CapturedParameter
 */
public interface UriParameter<V> extends Serializable {

    /**
     * This method is called during the interpretation process of a URI fragment and has the task of retrieving all
     * parameter values from the given map which belong to this parameter. When a URI fragment is interpreted, all
     * values belonging to registered URI parameters are collected in a map where the keys correspond to parameter
     * names, and the values correspond to the respective parameter values. A URI parameter object is responsible for
     * picking all the parameter values from this map for which it is responsible. As a result, an object of type {@link
     * ParameterValue} is returned which contains the parameter value readily converted into the correct domain type. If
     * an error occurred during the conversion of the parameter value(s) or if no value could be found for a
     * non-optional parameter, {@link ParameterValue} object is returned without a value and its {@link
     * UriParameterError} set accordingly.
     *
     * @param parameters all parameters found in the currently interpreted URI fragment
     * @return if all necessary values could be found in the given map an instance of class {@link ParameterValue} that
     * contains the value for this parameter converted in the correct domain type is returned. If an error occurred
     * during the conversion process or if no value could be found in the given map for a non-optional parameter, a
     * {@link ParameterValue} is returned which is correspondingly configured (i. e. the {@link UriParameterError} is
     * set respectively). Must not return <code>null</code>.
     * @see AbstractUriParameter
     */
    ParameterValue<V> consumeParameters(Map<String, String> parameters);

    /**
     * Returns the id for this parameter. This id is used to uniquely identify a {@link ParameterValue} for this URI
     * parameter in a {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues} object.
     */
    String getId();

    /**
     * Used to define a default value for this parameter which is used as the {@link ParameterValue} when no value could
     * be found for this parameter in an interpreted URI fragment. If a default value is set for a URI parameter, it is
     * considered optional, i. e. {@link #isOptional()} returns true and {@link #getDefaultValue()} returns the default
     * value specified with this method.
     *
     * @param defaultValue the default value used as the {@link ParameterValue} if no value could be found for this
     *                     parameter in a URI fragment
     */
    void setOptional(V defaultValue);

    /**
     * Returns true if a default value has been specified for this URI parameter with {@link #setOptional(Object)}.
     */
    boolean isOptional();

    /**
     * Returns the default value for this parameter provided with {@link #setOptional(Object)}.
     *
     * @return the default value or {@code null} if no default value was provided
     */
    V getDefaultValue();

    /**
     * Returns the number of individual parameter values managed by this URI parameter. This value is 1 for
     * single-valued parameters and a larger number for multi-valued parameters. This number has to concur with the size
     * of the list returned by {@link #getParameterNames()}.
     *
     * @return the number of individual parameter values managed by this URI parameter
     */
    int getSingleValueCount();

    /**
     * Returns the names of the individual parameter values managed by this URI parameter. For single-valued parameters
     * this list contains exactly one value which coincides with the parameter's id. For multi-valued parameters, this
     * list contains the names of the individual parameter values.
     * <p>
     * The order of names in the returned list is relevant for assembling and interpreting a URI fragment using {@link
     * ParameterMode#DIRECTORY}. When assembling a URI fragment that contains a multi-valued parameter, the individual
     * parameter values for this parameter appear in the order specified by this list.
     * <p>
     * If, for example, the two parameter values of a {@link Point2DUriParameter} are referred to with the two names
     * <tt>x</tt> and <tt>y</tt>, the list returned by this method for this parameter is <tt>["x", "y"]</tt>. For the
     * following URI fragment
     * <p>
     * <tt>#!/location/1.0/2.0</tt>
     * <p>
     * interpreted with {@link ParameterMode#DIRECTORY} the value 1.0 will be assigned to parameter name <tt>x</tt> and
     * 2.0 will be assigned to parameter name <tt>y</tt>.
     *
     * @return the names of the individual parameter values managed by this URI parameter
     */
    List<String> getParameterNames();

    /**
     * This method is the opposite operation for {@link #consumeParameters(Map)} in that it adds all values for this
     * parameter to a given list of Strings containing parameter names and values. The concrete value to add to the list
     * is given by the {@link ParameterValue} provided as the first argument. Depending on the specified {@link
     * ParameterMode}, the returned list either includes the parameter names (for {@link
     * ParameterMode#DIRECTORY_WITH_NAMES} and {@link ParameterMode#QUERY}) or not (for {@link
     * ParameterMode#DIRECTORY}.
     * <p>
     * This method must not alter the given list of URI tokens in any way except appending new values to it. The list
     * may already contain values from other URI parameters.
     * <p>
     * If this parameter is single-valued and the given parameter mode requires parameter names to be added to the list
     * the parameter's id has to be added first to the list followed by the given {@link ParameterValue} converted into
     * a String by this parameter's {@link ParameterValueConverter}. If this parameter is multi-valued the parameter
     * values are added to the list in the order defined by {@link #getParameterNames()}.
     * <p>
     * <p>
     * For example, if this method is called on a {@link Point2DUriParameter} created with
     * <pre>
     *     Point2DUriParameter coords = new Point2DUriParameter("coords", "x", "y");
     * </pre>
     * with a {@link ParameterValue} created for a {@link java.awt.geom.Point2D.Double} as follows
     * <pre>
     *     ParameterValue&lt;Point2D.Double&gt; value = ParameterValue.forValue(new Point2D.Double(17.0, 42.0))
     * </pre>
     * then the following sub-list has to be added to the list of URI tokens for {@link
     * ParameterMode#DIRECTORY_WITH_NAMES} and {@link ParameterMode#QUERY}:
     * <pre>
     *     {"x", "17.0", "y", "42.0"}
     * </pre>
     * If {@link ParameterMode#DIRECTORY} is used the parameter names are omitted from the list:
     * <pre>
     *     {"17.0", "42.0"}
     * </pre>
     *
     * @param value         the parameter value to be converted and added to the String list
     * @param uriTokens     the list of Strings to which the given parameter value is to be added
     * @param parameterMode required parameter mode to be used
     */
    void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, ParameterMode parameterMode);

    /**
     * Sets a new {@link ParameterValueConverter} for this URI parameter. The given converter replaces any existing
     * converter for this parameter.
     *
     * @param converter an implementation of interface {@link ParameterValueConverter} with the same domain type as this
     *                  parameter
     */
    void setConverter(ParameterValueConverter<V> converter);

    /**
     * Returns the {@link ParameterValueConverter} which is in use for this URI parameter.
     */
    ParameterValueConverter<V> getConverter();
}
