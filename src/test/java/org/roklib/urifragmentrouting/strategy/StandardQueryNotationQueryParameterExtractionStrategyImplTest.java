package org.roklib.urifragmentrouting.strategy;

import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class StandardQueryNotationQueryParameterExtractionStrategyImplTest {

    private StandardQueryNotationQueryParameterExtractionStrategyImpl strategy;

    @Before
    public void setUp() {
        strategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
    }

    @Test
    public void testExtractQueryParameters() throws Exception {
        String uriFragment = "/path/to/action?parameter_A=value_1&parameter_B=value_2";
        Map<String, String> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(2));
        assertThat(result.get("parameter_A"), is(equalTo("value_1")));
        assertThat(result.get("parameter_B"), is(equalTo("value_2")));
    }

    @Test
    public void testExtractQueryParameters_values_are_decoded() throws Exception {
        String parameterName = "?=&";
        String parameterValue = "%#";

        String uriFragment = "/path/to/action?" + URLEncoder.encode(parameterName, "UTF-8") + "=" + URLEncoder.encode(parameterValue, "UTF-8");
        Map<String, String> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(1));
        assertThat(result.get(parameterName), is(equalTo(parameterValue)));
    }

    @Test
    public void testExtractQueryParameters_parameters_without_explicit_values() throws Exception {
        String uriFragment = "/path/to/action?parameter_A&parameter_B=";
        Map<String, String> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(2));
        assertThat(result.get("parameter_A"), is(equalTo("")));
        assertThat(result.get("parameter_B"), is(equalTo("")));
    }

    @Test
    public void testExtractQueryParameters_parameter_value_contains_equals_sign() throws Exception {
        String uriFragment = "/path/to/action?parameter=value=extra";
        Map<String, String> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(1));
        assertThat(result.get("parameter"), is(equalTo("value=extra")));
    }

    @Test
    public void testExtractQueryParameters_no_parameters_set() throws Exception {
        assertThat(strategy.extractQueryParameters("/path/to/action").isEmpty(), is(true));
        assertThat(strategy.extractQueryParameters("/path/to/action?").isEmpty(), is(true));
        assertThat(strategy.extractQueryParameters("/path/to/action?    ").isEmpty(), is(true));
    }

    @Test
    public void testStripQueryParametersFromUriFragment_no_parameters_set() throws Exception {
        String uriFragment = "/path/to/action";
        assertThat(strategy.stripQueryParametersFromUriFragment(uriFragment), is(equalTo(uriFragment)));
    }

    @Test
    public void testStripQueryParametersFromUriFragment_empty_uri_fragment() throws Exception {
        String uriFragment = "";
        assertThat(strategy.stripQueryParametersFromUriFragment(uriFragment), is(equalTo(uriFragment)));
    }

    @Test
    public void testStripQueryParametersFromUriFragment() throws Exception {
        String uriFragment = "/path/to/action?parameter=value";
        String expectedResult = "/path/to/action";
        assertThat(strategy.stripQueryParametersFromUriFragment(uriFragment), is(equalTo(expectedResult)));
    }

    @Test
    public void testStripQueryParametersFromUriFragment_separator_char_is_removed() throws Exception {
        String uriFragment = "/path/to/action?";
        String expectedResult = "/path/to/action";
        assertThat(strategy.stripQueryParametersFromUriFragment(uriFragment), is(equalTo(expectedResult)));
    }

    @Test
    public void assemble_query_for_null_map() {
        assertThat(strategy.assembleQueryParameterSectionForUriFragment(null), is(equalTo("")));
    }

    @Test
    public void assemble_query_for_empty_map() {
        assertThat(strategy.assembleQueryParameterSectionForUriFragment(Collections.emptyMap()), is(equalTo("")));
    }

    @Test
    public void assemble_query_for_parameter_map_with_correct_encoding() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "17");
        parameters.put("lang", "de en");
        parameters.put("number", "one#two");
        final String querySection = strategy.assembleQueryParameterSectionForUriFragment(parameters);

        assertThat(querySection + " doesn't match expected regex",
                querySection.matches("^\\?((id=17|lang=de%20en|number=one%23two)&?){3}$"), is(true));
    }

    @Test
    public void assemble_query_for_parameter_map_reserved_characters_are_encoded_properly() {
        Map<String, String> values = new HashMap<>();
        values.put("id", "value%26with&reserved=characters");

        final String querySection = strategy.assembleQueryParameterSectionForUriFragment(values);
        final Map<String, String> result = strategy.extractQueryParameters(querySection);
        assertThat(result.get("id"), is(equalTo(values.get("id"))));
    }
}