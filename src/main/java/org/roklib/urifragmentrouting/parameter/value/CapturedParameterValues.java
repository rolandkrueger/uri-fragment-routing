package org.roklib.urifragmentrouting.parameter.value;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.UriParameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages a set of {@link ParameterValue} objects and offers a number of data access methods which make
 * working with this kind of data easy. Instances of this class are needed for two situations: when a URI fragment is
 * interpreted by the {@link org.roklib.urifragmentrouting.UriActionMapperTree} and when a parameterized URI fragment is
 * created by {@link org.roklib.urifragmentrouting.UriActionMapperTree#assembleUriFragment(CapturedParameterValues,
 * UriPathSegmentActionMapper)} . With the former usage, all parameter values found in the currently interpreted URI
 * fragment are put into an instance of {@link CapturedParameterValues} which is later used to pass on these values to
 * the {@link org.roklib.urifragmentrouting.UriActionCommand} executed for the URI fragment. With the latter usage, the
 * {@link CapturedParameterValues} are used to define all parameter values to encode into a generated URI fragment used
 * to create links.
 * <p>
 * In order to put a parameter value into an object of this class or to retrieve a value from it, two pieces of
 * information are needed: the id of the parameter itself and the name of the {@link AbstractUriPathSegmentActionMapper}
 * on which the respective parameter is registered. This is necessary since the same URI fragment parameter can be
 * registered on two different action mappers.
 * <p>
 * Take for example the following URI:
 * <p>
 * <tt> http://www.example.com#!/products/productId/17/view/expand/reviews</tt>
 * <p>
 * There are two action mappers with names <tt>products</tt> and <tt>view</tt>. Mapper <tt>products</tt> has one
 * registered URI fragment parameter with id <tt>productId</tt>, while mapper <tt>view</tt> has another parameter with
 * id <tt>expand</tt>. To set a value for <tt>productId</tt> you use the following code:
 * <p>
 * <code>capturedParameterValues.setValueFor("products", "productId", ParameterValue&lt;Integer&gt;.forValue(17));
 * </code>
 * <p>
 * In order to read the value for <tt>expand</tt>, the following code is used:
 * <p>
 * <code> ParameterValue&lt;String&gt; value = capturedParameterValues.getValueFor("view", "expand"); </code>
 */
public class CapturedParameterValues {

    private final Map<String, Map<String, ParameterValue<?>>> values;

    public CapturedParameterValues() {
        values = new HashMap<>();
    }

    /**
     * Retrieves the {@link ParameterValue} for a URI fragment parameter registered on a particular action mapper as
     * specified by the method arguments. Returns <code>null</code> if there is no such value available.
     *
     * @param mapperName  name of the action mapper on which the queried URI fragment parameter is registered
     * @param parameterId id of the queried URI fragment parameter
     * @param <V>         data type of the parameter value
     * @return a {@link ParameterValue} object for the specified action mapper and parameter or <code>null</code> if no
     * parameter value is available
     * @throws NullPointerException if either argument is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public <V> ParameterValue<V> getValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return null;
        }

        return (ParameterValue<V>) parameterValues.get(parameterId);
    }

    /**
     * Sets the {@link ParameterValue} for a URI fragment parameter registered on a particular action mapper as
     * specified by the method arguments. If the {@link ParameterValue} object is null this method does nothing.
     *
     * @param mapperName  name of the action mapper on which the URI fragment parameter is registered
     * @param parameterId id of the URI fragment parameter for which the value is to be set
     * @param value       the parameter value to be set
     * @param <V>         data type of the parameter value
     * @throws NullPointerException if either the mapper name or the parameter id argument is <code>null</code>
     */
    public <V> void setValueFor(String mapperName, String parameterId, ParameterValue<?> value) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);
        if (value == null) {
            return;
        }

        final Map<String, ParameterValue<?>> mapperValues = values.computeIfAbsent(mapperName, k -> new HashMap<>());
        mapperValues.put(parameterId, value);
    }

    /**
     * Sets the {@link ParameterValue} for a URI fragment parameter registered on a particular action mapper as
     * specified by the method arguments. If the {@link ParameterValue} object is null this method does nothing.
     *
     * @param mapperName name of the action mapper on which the URI fragment parameter is registered
     * @param parameter  URI fragment parameter for which the value is to be set
     * @param value      the parameter value to be set
     * @param <V>        data type of the parameter value
     */
    public <V> void setValueFor(String mapperName, UriParameter<V> parameter, ParameterValue<?> value) {
        setValueFor(mapperName, parameter.getId(), value);
    }

    /**
     * Returns true if this {@link CapturedParameterValues} object is empty, i. e. it does not contain any parameter
     * values.
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> asQueryParameterMap() {
        if (values.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>();
        values.values().forEach(stringParameterValueMap -> stringParameterValueMap.entrySet().forEach(stringParameterValueEntry -> result.put(stringParameterValueEntry.getKey(), stringParameterValueEntry.getValue().getValue().toString())));
        return result;
    }

    /**
     * Removes the parameter value for the given action mapper and URI fragment parameter from this object and returns
     * the removed value.
     *
     * @param mapperName  name of the action mapper on which the URI fragment parameter is registered
     * @param parameterId URI fragment parameter for which the value is to be removed
     * @param <V>         data type of the parameter value to be removed
     * @return the removed parameter value or <code>null</code> if no parameter value was found for the given action
     * mapper and parameter id
     */
    public <V> ParameterValue<V> removeValueFor(String mapperName, String parameterId) {
        final ParameterValue<V> value = getValueFor(mapperName, parameterId);
        if (value != null) {
            final Map<String, ParameterValue<?>> mapperParameters = values.get(mapperName);
            mapperParameters.remove(parameterId);
            if (mapperParameters.isEmpty()) {
                values.remove(mapperName);
            }
        }
        return value;
    }

    /**
     * Check if there is a parameter value available for the specified action mapper and parameter.
     *
     * @param mapperName  name of an action mapper
     * @param parameterId id of the queried parameter
     * @return true if a parameter value can be retrieved from this instance for the given action mapper name and
     * parameter id
     */
    public boolean hasValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return false;
        }
        ParameterValue<?> parameterValue = parameterValues.get(parameterId);
        return parameterValue != null && parameterValue.hasValue();
    }

    @Override
    public String toString() {
        return "CapturedParameterValues{" +
                "values=" + values +
                '}';
    }
}
