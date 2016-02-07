package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class CapturedParameterValuesImplTest {

    private CapturedParameterValuesImpl values;
    private SingleStringUriParameter stringTextParameter;
    private SingleStringUriParameter stringNameParameter;
    private SingleIntegerUriParameter integerParameter;
    private StringListUriParameter stringListParameter;

    @Before
    public void setUp() {
        values = new CapturedParameterValuesImpl();
        stringTextParameter = new SingleStringUriParameter("text");
        stringNameParameter = new SingleStringUriParameter("name");
        integerParameter = new SingleIntegerUriParameter("number");
        stringListParameter = new StringListUriParameter("list");
    }

    @Test
    public void returns_null_for_unknown_mapper_name() {
        assertThat(values.getValueFor("unknown", stringTextParameter.getId()), is(nullValue()));
    }

    @Test
    public void returns_null_for_unavailable_parameter() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("test"));
        assertThat(values.getValueFor("first", integerParameter.getId()), is(nullValue()));
    }

    @Test
    public void getValueFor_returns_correct_value() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        values.setValueFor("first", stringNameParameter, ParameterValue.forValue("nameValue"));
        values.setValueFor("second", integerParameter, ParameterValue.forValue(17));

        assertThat(values.getValueFor("first", stringTextParameter.getId()).getValue(), equalTo("textValue"));
        assertThat(values.getValueFor("first", stringNameParameter.getId()).getValue(), equalTo("nameValue"));
        assertThat(values.getValueFor("second", integerParameter.getId()).getValue(), equalTo(17));
    }

    @Test(expected = NullPointerException.class)
    public void getValueFor_mapper_name_null_not_allowed() {
        values.getValueFor(null, stringTextParameter.getId());
    }

    @Test(expected = NullPointerException.class)
    public void getValueFor_parameter_null_not_allowed() {
        values.getValueFor("first", null);
    }

    @Test(expected = NullPointerException.class)
    public void setValueFor_mapper_name_null_not_allowed() {
        values.setValueFor(null, stringTextParameter, ParameterValue.forValue(""));
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void setValueFor_parameter_null_not_allowed() {
        values.setValueFor("first", (UriParameter) null, ParameterValue.forValue(""));
    }

    @Test
    public void testHasValueFor_with_absent_value_object() {
        assertThat(values.hasValueFor("unknown", stringNameParameter.getId()), is(false));
    }

    @Test
    public void testHasValueFor_with_erroneous_value() {
        values.setValueFor("first", stringNameParameter, ParameterValue.forError(UriParameterError.CONVERSION_ERROR));
        assertThat(values.hasValueFor("first", stringNameParameter.getId()), is(false));
        assertThat(values.getValueFor("first", stringNameParameter.getId()).hasError(), is(true));
    }

    @Test
    public void testHasValueFor_with_available_value() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        assertThat(values.hasValueFor("first", stringTextParameter.getId()), is(true));
    }

    @Test
    public void testIsEmpty() {
        assertThat(values.isEmpty(), is(true));

        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        assertThat(values.isEmpty(), is(false));
    }

    @Test
    public void testAsQueryParamMap_with_empty_values() {
        assertThat(new CapturedParameterValuesImpl().asQueryParameterMap().isEmpty(), is(true));
    }

    @Test
    public void testAsQueryParamMap() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        values.setValueFor("first", stringNameParameter, ParameterValue.forValue("nameValue"));
        values.setValueFor("second", integerParameter, ParameterValue.forValue(17));
        values.setValueFor("second", stringListParameter, ParameterValue.forValue(Arrays.asList("a", "b")));
        final Map<String, List<String>> resultMap = values.asQueryParameterMap();
        assertThat(resultMap.size(), is(4));
        assertThat(resultMap.get("text"), hasSize(1));
        assertThat(resultMap.get("text"), hasItem("textValue"));
        assertThat(resultMap.get("name"), hasSize(1));
        assertThat(resultMap.get("name"), hasItem("nameValue"));
        assertThat(resultMap.get("number"), hasSize(1));
        assertThat(resultMap.get("number"), hasItem("17"));
        assertThat(resultMap.get("list"), hasSize(2));
        assertThat(resultMap.get("list"), hasItems("a", "b"));
    }
}
