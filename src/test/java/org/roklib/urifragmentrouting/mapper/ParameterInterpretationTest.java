package org.roklib.urifragmentrouting.mapper;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.parameter.*;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParameterInterpretationTest {

    public static final String MAPPER_NAME = "mapper";

    private AbstractUriPathSegmentActionMapper.ParameterInterpreter interpreter;
    private CapturedParameterValues consumedValues;
    private Map<String, UriParameter<?>> registeredUriParameters;
    private Set<String> registeredUriParameterNames;
    private Map<String, String> queryParameters;
    private SingleStringUriParameter nameParameter;
    private SingleIntegerUriParameter idParameter;
    private Point2DUriParameter pointParameter;

    @Before
    public void setUp() {
        interpreter = new AbstractUriPathSegmentActionMapper.ParameterInterpreter(MAPPER_NAME);
        consumedValues = new CapturedParameterValues();

        nameParameter = new SingleStringUriParameter("name");
        idParameter = new SingleIntegerUriParameter("id");
        pointParameter = new Point2DUriParameter("point", "x", "y");

        registeredUriParameters = new LinkedHashMap<>();
        registeredUriParameters.put(nameParameter.getId(), nameParameter);
        registeredUriParameters.put(idParameter.getId(), idParameter);
        registeredUriParameters.put(pointParameter.getId(), pointParameter);

        registeredUriParameterNames = new HashSet<>();
        registeredUriParameterNames.addAll(Arrays.asList("name", "id", "x", "y"));

        queryParameters = new HashMap<>();
    }

    @Test
    public void consume_one_named_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("name", "test", "unregistered", "parameter", "id", "17"));
        final CapturedParameterValues result = interpreter.interpretDirectoryParameters(registeredUriParameterNames, registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIsAbsent(result, idParameter);
        assertParameterValueIsAbsent(result, pointParameter);
    }

    @Test
    public void consume_all_named_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("x", "10.12345", "name", "test", "id", "17", "y", "20.56789"));

        final CapturedParameterValues result = interpreter.interpretDirectoryParameters(registeredUriParameterNames,
                registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIs(result, idParameter, 17);
        Point2D.Double point = new Point2D.Double(10.12345d, 20.56789d);
        assertParameterValueIs(result, pointParameter, point);
    }

    @Test
    public void consume_one_nameless_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Collections.singletonList("test"));
        final CapturedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIsAbsent(result, idParameter);
        assertParameterValueIsAbsent(result, pointParameter);
    }

    @Test
    public void consume_all_nameless_directory_parameters_successfully() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("test", "17", "10.12345", "20.56789"));
        final CapturedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIs(result, idParameter, 17);
        Point2D.Double point = new Point2D.Double(10.12345d, 20.56789d);
        assertParameterValueIs(result, pointParameter, point);
    }

    @Test
    public void nameless_directory_parameters_in_wrong_order_dont_work() {
        List<String> uriTokens = new ArrayList<>(Arrays.asList("10.12345", "20.56789", "17", "test", "10.12345", "20.56789"));
        final CapturedParameterValues result = interpreter.interpretNamelessDirectoryParameters(registeredUriParameters, consumedValues, uriTokens);
        assertParameterValueIs(result, nameParameter, "10.12345");
        assertParameterValueIsAbsent(result, idParameter);
        assertParameterValueIsAbsent(result, pointParameter);
    }

    @Test
    public void consume_empty_set_of_query_parameters() {
        CapturedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, Collections.emptyMap());
        assertParameterValueIsAbsent(consumedValues, nameParameter);
        assertParameterValueIsAbsent(consumedValues, idParameter);
        assertParameterValueIsAbsent(consumedValues, pointParameter);
    }

    @Test
    public void consume_one_query_parameter_successfully() {
        addQueryParameter("name", "test");
        CapturedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
        assertParameterValueIs(result, nameParameter, "test");
        assertParameterValueIsAbsent(result, idParameter);
        assertParameterValueIsAbsent(result, pointParameter);
    }

    @Test
    public void consume_all_query_parameters_successfully() {
        addQueryParameter("name", "test");
        addQueryParameter("id", "17");
        addQueryParameter("x", "10.12345");
        addQueryParameter("y", "20.56789");

        CapturedParameterValues result = interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
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

    @Test
    public void missing_non_optional_parameter_yields_erroneous_value_object() {
        interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);

        ParameterValue<String> nameValue = consumedValues.getValueFor(MAPPER_NAME, nameParameter.getId());
        assertThat(nameValue, is(notNullValue()));
        MatcherAssert.assertThat(nameValue.getError(), equalTo(UriParameterError.PARAMETER_NOT_FOUND));
    }

    @Test
    public void missing_optional_parameter_yields_default_value() {
        SingleStringUriParameter parameterWithDefaultValue = new SingleStringUriParameter("parameter");
        parameterWithDefaultValue.setOptional("default");
        registeredUriParameters.put(parameterWithDefaultValue.getId(), parameterWithDefaultValue);

        interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
        assertParameterValueIs(consumedValues, parameterWithDefaultValue, "default");
    }

    private CapturedParameterValues interpretQueryParameters(Map<String, UriParameter<?>> registeredUriParameters,
                                                             CapturedParameterValues consumedValues,
                                                             Map<String, String> queryParameters) {
        return interpreter.interpretQueryParameters(registeredUriParameters, consumedValues, queryParameters);
    }

    private void addQueryParameter(String name, String value) {
        queryParameters.put(name, value);
    }

    private <V> void assertParameterValueIs(CapturedParameterValues values, UriParameter<V> parameter, V expectedValue) {
        ParameterValue<V> value = values.getValueFor(MAPPER_NAME, parameter.getId());
        assertThat("expected value is not present in Optional", value, is(notNullValue()));
        assertThat("interpreted value does not meet expectation", value.getValue(), is(expectedValue));
    }

    private void assertParameterValueIsAbsent(CapturedParameterValues values, UriParameter<?> parameter) {
        assertThat("parameter expected to be absent is found", values.hasValueFor(MAPPER_NAME, parameter.getId()), is(false));
    }
}
