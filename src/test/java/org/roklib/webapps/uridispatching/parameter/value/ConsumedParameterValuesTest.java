package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class ConsumedParameterValuesTest {

    private ConsumedParameterValues values;
    private SingleStringURIParameter stringTextParameter;
    private SingleStringURIParameter stringNameParameter;
    private SingleIntegerURIParameter integerParameter;

    @Before
    public void setUp() {
        values = new ConsumedParameterValues();
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
        values.setValueFor("first", stringTextParameter, "test");
        assertThat(values.getValueFor("first", integerParameter), equalTo(Optional.empty()));
    }

    @Test
    public void getValueFor_returns_correct_value() {
        values.setValueFor("first", stringTextParameter, "textValue");
        values.setValueFor("first", stringNameParameter, "nameValue");
        values.setValueFor("second", integerParameter, 17);

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
        values.setValueFor(null, stringTextParameter, "");
    }

    @Test(expected = NullPointerException.class)
    public void setValueFor_parameter_null_not_allowed() {
        values.setValueFor("first", null, "");
    }
}
