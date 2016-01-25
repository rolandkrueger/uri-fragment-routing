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
    private SingleStringURIParameter stringParameter;
    private SingleIntegerURIParameter integerParameter;

    @Before
    public void setUp() {
        values = new ConsumedParameterValues();
        stringParameter = new SingleStringURIParameter("text");
        integerParameter = new SingleIntegerURIParameter("number");
    }

    @Test
    public void returns_empty_optional_for_unknown_mapper_name() {
        assertThat(values.getValueFor("unknown", stringParameter), equalTo(Optional.empty()));
    }

    @Test
    public void returns_empty_optional_for_unavailable_parameter() {
        values.setValueFor("first", stringParameter, "test");
        assertThat(values.getValueFor("first", integerParameter), equalTo(Optional.empty()));
    }

    @Test
    public void getValueFor_returns_correct_value() {
        values.setValueFor("first", stringParameter, "test");
        values.setValueFor("second", integerParameter, 17);

        assertThat(values.getValueFor("first", stringParameter).get().getValue(), equalTo("test"));
        assertThat(values.getValueFor("second", integerParameter).get().getValue(), equalTo(17));
    }

    @Test(expected = NullPointerException.class)
    public void getValueFor_mapper_name_null_not_allowed() {
        values.getValueFor(null, stringParameter);
    }

    @Test(expected = NullPointerException.class)
    public void getValueFor_parameter_null_not_allowed() {
        values.getValueFor("first", null);
    }

    @Test(expected = NullPointerException.class)
    public void setValueFor_mapper_name_null_not_allowed() {
        values.setValueFor(null, stringParameter, "");
    }

    @Test(expected = NullPointerException.class)
    public void setValueFor_parameter_null_not_allowed() {
        values.setValueFor("first", null, "");
    }
}
