package org.roklib.webapps.uridispatching;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.Point2DURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;
import org.roklib.webapps.uridispatching.parameter.URIParameter;
import org.roklib.webapps.uridispatching.parameter.value.ConsumedParameterValues;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ParameterInterpretationTest {

    public static final String MAPPER_NAME = "mapper";

    private AbstractURIPathSegmentActionMapper.ParameterInterpreter interpreter;
    private ConsumedParameterValues consumedValues;
    private List<URIParameter<?>> registeredUriParameters;
    private Set<String> registeredUriParameterNames;
    private Map<String, List<String>> queryParameters;
    private SingleStringURIParameter nameParameter;
    private SingleIntegerURIParameter idParameter;
    private Point2DURIParameter pointParameter;

    @Before
    public void setUp() {
        interpreter = new AbstractURIPathSegmentActionMapper.ParameterInterpreter(MAPPER_NAME);
        consumedValues = new ConsumedParameterValues();

        nameParameter = new SingleStringURIParameter("name");
        idParameter = new SingleIntegerURIParameter("id");
        pointParameter = new Point2DURIParameter("x", "y");

        registeredUriParameters = new LinkedList<>();
        registeredUriParameters.add(nameParameter);
        registeredUriParameters.add(idParameter);
        registeredUriParameters.add(pointParameter);

        registeredUriParameterNames = new HashSet<>();
        registeredUriParameterNames.addAll(Arrays.asList("name", "id", "x", "y"));

        queryParameters = new HashMap<>();
    }

    @Test
    public void consume_one_named_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("name", "test", "unregistered", "parameter", "id", "17"));
        final ConsumedParameterValues result = interpreter.interpretDirectoryParameters(registeredUriParameterNames, registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIsAbsent(result, idParameter);
    }

    @Test
    public void consume_all_named_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("x", "10.12345", "name", "test", "id", "17", "y", "20.56789"));

        final ConsumedParameterValues result = interpreter.interpretDirectoryParameters(registeredUriParameterNames,
                registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIs(result, idParameter, 17);
        Point2D.Double point = new Point2D.Double(10.12345d, 20.56789d);
        assertParameterValueIs(result, pointParameter, point);
    }

    @Test
    public void consume_one_nameless_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Collections.singletonList("test"));
        final ConsumedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
    }

    @Test
    public void consume_all_nameless_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("test", "17", "10.12345", "20.56789"));
        final ConsumedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIs(result, idParameter, 17);
        Point2D.Double point = new Point2D.Double(10.12345d, 20.56789d);
        assertParameterValueIs(result, pointParameter, point);
    }

    @Test
    public void nameless_directory_parameters_in_wrong_order_dont_work() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("10.12345", "20.56789", "17", "test", "10.12345", "20.56789"));
        final ConsumedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "10.12345");
        assertParameterValueIsAbsent(result, idParameter);
        assertParameterValueIsAbsent(result, pointParameter);
    }

    @Test
    public void consume_empty_set_of_query_parameters() {
        ConsumedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, Collections.emptyMap());
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void consume_one_query_parameter_successfully() {
        addQueryParameter("name", "test");
        ConsumedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
        assertParameterValueIs(result, nameParameter, "test");
    }

    @Test
    public void consume_all_query_parameters_successfully() {
        addQueryParameter("name", "test");
        addQueryParameter("id", "17");
        addQueryParameter("x", "10.12345");
        addQueryParameter("y", "20.56789");

        ConsumedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIs(result, idParameter, 17);
        Point2D.Double point = new Point2D.Double(10.12345d, 20.56789d);
        assertParameterValueIs(result, pointParameter, point);
    }

    @Test
    public void consumed_query_parameter_values_are_removed_from_value_map() {
        addQueryParameter("name", "test");
        addQueryParameter("id", "17");
        addQueryParameter("unknown", "unknown");

        interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);

        assertThat(queryParameters.size(), is(1));
        assertThat(queryParameters.containsKey("unknown"), is(true));
    }

    private ConsumedParameterValues interpretQueryParameters(List<URIParameter<?>> registeredUriParameters,
                                                             ConsumedParameterValues consumedValues,
                                                             Map<String, List<String>> queryParameters) {
        return interpreter.interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
    }

    private void addQueryParameter(String name, String value) {
        queryParameters.computeIfAbsent(name,
                k -> new ArrayList<>())
                .add(value);
    }

    private <V> void assertParameterValueIs(ConsumedParameterValues values, URIParameter<V> parameter, V expectedValue) {
        Optional<ParameterValue<V>> valueOptional = values.getValueFor(MAPPER_NAME, parameter);
        assertThat("expected value is not present in Optional", valueOptional.isPresent(), is(true));
        assertThat("interpreted value does not meet expectation", valueOptional.get().getValue(), is(expectedValue));
    }

    private void assertParameterValueIsAbsent(ConsumedParameterValues values, URIParameter<?> parameter) {
        assertThat("parameter expected to be absent is found", values.hasValueFor(MAPPER_NAME, parameter), is(false));
    }
}
