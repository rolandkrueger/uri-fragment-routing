package org.roklib.webapps.uridispatching.strategy;

import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
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
        Map<String, List<String>> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(2));
        assertThat(result.get("parameter_A"), hasItems("value_1"));
        assertThat(result.get("parameter_B"), hasItems("value_2"));
    }

    @Test
    public void testExtractQueryParameters_values_are_decoded() throws Exception {
        String parameterName = "?=&";
        String parameterValue = "%#";

        String uriFragment = "/path/to/action?" + URLEncoder.encode(parameterName, "UTF-8") + "=" + URLEncoder.encode(parameterValue, "UTF-8");
        Map<String, List<String>> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(1));
        assertThat(result.get(parameterName), hasItems(parameterValue));
    }

    @Test
    public void testExtractQueryParameters_multi_valued_parameter() throws Exception {
        String uriFragment = "/path/to/action?parameter=value_1&parameter=value_2";
        Map<String, List<String>> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(1));
        assertThat(result.get("parameter"), hasItems("value_1"));
        assertThat(result.get("parameter"), hasItems("value_2"));
    }

    @Test
    public void testExtractQueryParameters_parameters_without_explicit_values() throws Exception {
        String uriFragment = "/path/to/action?parameter_A&parameter_B=";
        Map<String, List<String>> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(2));
        assertThat(result.get("parameter_A"), hasItems(""));
        assertThat(result.get("parameter_B"), hasItems(""));
    }

    @Test
    public void testExtractQueryParameters_parameter_value_contains_equals_sign() throws Exception {
        String uriFragment = "/path/to/action?parameter=value=extra";
        Map<String, List<String>> result = strategy.extractQueryParameters(uriFragment);
        assertThat(result.size(), is(1));
        assertThat(result.get("parameter"), hasItems("value=extra"));
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
}