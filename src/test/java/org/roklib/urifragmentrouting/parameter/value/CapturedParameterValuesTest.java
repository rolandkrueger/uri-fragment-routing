package org.roklib.urifragmentrouting.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.parameter.*;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CapturedParameterValuesTest {

    private CapturedParameterValues values;
    private SingleStringUriParameter stringTextParameter;
    private SingleStringUriParameter stringNameParameter;
    private SingleIntegerUriParameter integerParameter;
    private StringListUriParameter stringListParameter;

    @Before
    public void setUp() {
        values = new CapturedParameterValues();
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
        assertThat(new CapturedParameterValues().asQueryParameterMap().isEmpty(), is(true));
    }

    @Test
    public void testAsQueryParamMap() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        values.setValueFor("first", stringNameParameter, ParameterValue.forValue("nameValue"));
        values.setValueFor("second", integerParameter, ParameterValue.forValue(17));
        values.setValueFor("second", stringListParameter, ParameterValue.forValue(Arrays.asList("a", "b")));
        Map<String, String> resultMap = values.asQueryParameterMap();
        assertThat(resultMap.size(), is(4));
        assertThat(resultMap.get("text"), is(equalTo("textValue")));
        assertThat(resultMap.get("name"), is(equalTo("nameValue")));
        assertThat(resultMap.get("number"), is(equalTo("17")));
    }

    @Test
    public void testRemoveValueFor() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        final ParameterValue<String> value = values.removeValueFor("first", stringTextParameter.getId());
        assertThat(value.getValue(), is("textValue"));
        assertThat(values.isEmpty(), is(true));
    }
}
