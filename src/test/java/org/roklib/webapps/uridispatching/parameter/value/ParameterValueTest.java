package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.UriParameterError;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ParameterValueTest {

    private ParameterValue<String> value;
    private ParameterValue<String> error;

    @Before
    public void setUp() {
        value = ParameterValue.forValue("value");
        error = ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
    }

    @Test(expected = NullPointerException.class)
    public void null_values_are_not_allowed() {
        ParameterValue.forValue((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_no_error_as_error_is_not_allowed() {
        ParameterValue.forError(UriParameterError.NO_ERROR);
    }

    @Test(expected = IllegalStateException.class)
    public void getValue_with_error_throws_exception() {
        error.getValue();
    }

    @Test
    public void test_hasValue_with_value() {
        assertThat(value.hasValue(), is(true));
    }

    @Test
    public void test_has_Value_with_error() {
        assertThat(error.hasValue(), is(false));
    }

    @Test
    public void test_hasError_with_value() {
        assertThat(value.hasError(), is(false));
    }

    @Test
    public void test_hasError_with_error() {
        assertThat(error.hasError(), is(true));
    }

    @Test
    public void test_default_value() {
        value = ParameterValue.forDefaultValue("default");
        assertThat(value.isDefaultValue(), is(true));
    }

}