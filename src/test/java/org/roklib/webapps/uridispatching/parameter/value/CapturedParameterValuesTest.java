package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;
import org.roklib.webapps.uridispatching.parameter.URIParameterError;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class CapturedParameterValuesTest {

    private CapturedParameterValues values;
    private SingleStringURIParameter stringTextParameter;
    private SingleStringURIParameter stringNameParameter;
    private SingleIntegerURIParameter integerParameter;

    @Before
    public void setUp() {
        values = new CapturedParameterValues();
        stringTextParameter = new SingleStringURIParameter("text");
        stringNameParameter = new SingleStringURIParameter("name");
        integerParameter = new SingleIntegerURIParameter("number");
    }

    @Test
    public void returns_empty_optional_for_unknown_mapper_name() {
        assertThat(values.getValueFor("unknown", stringTextParameter), equalTo(Optional.empty()));
    }

    @Test
    public void returns_empty_optional_for_unavailable_parameter() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("test"));
        assertThat(values.getValueFor("first", integerParameter), equalTo(Optional.empty()));
    }

    @Test
    public void getValueFor_returns_correct_value() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        values.setValueFor("first", stringNameParameter, ParameterValue.forValue("nameValue"));
        values.setValueFor("second", integerParameter, ParameterValue.forValue(17));

        assertThat(values.getValueFor("first", stringTextParameter).get().getValue(), equalTo("textValue"));
        assertThat(values.getValueFor("first", stringNameParameter).get().getValue(), equalTo("nameValue"));
        assertThat(values.getValueFor("second", integerParameter).get().getValue(), equalTo(17));
    }

    @Test(expected = NullPointerException.class)
    public void getValueFor_mapper_name_null_not_allowed() {
        values.getValueFor(null, stringTextParameter);
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
    public void setValueFor_parameter_null_not_allowed() {
        values.setValueFor("first", null, ParameterValue.forValue(""));
    }

    @Test
    public void testHasValueFor_with_absent_value_object() {
        assertThat(values.hasValueFor("unknown", stringNameParameter), is(false));
    }

    @Test
    public void testHasValueFor_with_erroneous_value() {
        values.setValueFor("first", stringNameParameter, ParameterValue.forError(URIParameterError.CONVERSION_ERROR));
        assertThat(values.hasValueFor("first", stringNameParameter), is(false));
        assertThat(values.getValueFor("first", stringNameParameter).get().hasError(), is(true));
    }

    @Test
    public void testHasValueFor_with_available_value() {
        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        assertThat(values.hasValueFor("first", stringTextParameter), is(true));
    }

    @Test
    public void testIsEmpty() {
        assertThat(values.isEmpty(), is(true));

        values.setValueFor("first", stringTextParameter, ParameterValue.forValue("textValue"));
        assertThat(values.isEmpty(), is(false));
    }
}
