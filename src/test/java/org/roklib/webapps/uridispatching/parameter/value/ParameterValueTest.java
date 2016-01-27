package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.parameter.URIParameterError;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ParameterValueTest {

    private ParameterValue<String> value;
    private ParameterValue<String> error;

    @Before
    public void setUp() {
        value = new ParameterValue<>("value");
        error = new ParameterValue<>(URIParameterError.CONVERSION_ERROR);
    }

    @Test(expected = NullPointerException.class)
    public void null_values_are_not_allowed() {
        new ParameterValue<>((String) null);
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

}