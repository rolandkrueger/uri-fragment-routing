package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.exception.InvalidActionCommandClassException;
import org.roklib.webapps.uridispatching.exception.InvalidMethodSignatureException;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerUriParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringUriParameter;
import org.roklib.webapps.uridispatching.parameter.annotation.AllCapturedParameters;
import org.roklib.webapps.uridispatching.parameter.annotation.CapturedParameter;
import org.roklib.webapps.uridispatching.parameter.annotation.CurrentUriFragment;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class PassCapturedParameterValuesToActionCommandTest {

    private CapturedParameterValuesImpl capturedParameterValues;
    private SingleStringUriParameter nameParameter;
    private SingleIntegerUriParameter intParameter;

    @Before
    public void setUp() throws Exception {
        capturedParameterValues = new CapturedParameterValuesImpl();
        nameParameter = new SingleStringUriParameter("nameParam");
        intParameter = new SingleIntegerUriParameter("intParam");
    }

    @Test
    public void new_instance_of_action_command_is_created() {
        final UriActionCommand action = capturedParameterValues.createActionCommandAndPassParameters(null,
                ActionCommandForSettingParametersAndUriFragment.class);

        assertThat(action, instanceOf(ActionCommandForSettingParametersAndUriFragment.class));
    }

    @Test
    public void set_one_captured_parameter() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        final UriActionCommand result = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
    }

    @Test
    public void no_parameters_available() {
        final UriActionCommand result = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.allValues.isEmpty(), is(true));
        assertThat(action.currentUriFragment, is(equalTo("currentUriFragment")));
        assertThat(action.nameValue, is(nullValue()));
    }

    @Test
    public void set_all_captured_parameters() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        final UriActionCommand result = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.allValues.isEmpty(), is(false));
        assertThat(action.allValues.getValueFor("mapper", "nameParam").getValue(), is("name"));
        assertThat(action.allValues.getValueFor("mapper", "intParam").getValue(), is(17));
    }

    @Test
    public void inherited_setter_methods_will_be_invoked() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        final UriActionCommand result = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment",
                InheritedActionCommand.class);
        InheritedActionCommand action = (InheritedActionCommand) result;
        assertThat(action.currentUriFragment, is(equalTo("currentUriFragment")));
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
        assertThat(action.integerValue.getValue(), is(equalTo(17)));
    }

    @Test
    public void set_current_uri_fragment() {
        final UriActionCommand action = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);

        assertThat(((ActionCommandForSettingParametersAndUriFragment) action).currentUriFragment, is(equalTo("currentUriFragment")));
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_with_private_visibility() {
        capturedParameterValues.createActionCommandAndPassParameters(null, ActionCommandWithPrivateVisibility.class);
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_without_default_constructor() {
        capturedParameterValues.createActionCommandAndPassParameters(null, ActionCommandWithoutDefaultConstructor.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_current_uri_fragment_is_private() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithPrivateUriFragmentSetter.class);
    }

    @Test
    public void use_action_command_without_any_setters() {
        final UriActionCommand result = capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithoutAnySetters.class);
        assertThat(result, is(instanceOf(ActionCommandWithoutAnySetters.class)));
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_type() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterTypeForParameterSetter.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_count() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterCountForParameterSetter.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_type() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterTypeForUriFragmentSetter.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_count() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterCountForUriFragmentSetter.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_type() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterTypeForAllValuesSetter.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_count() {
        capturedParameterValues.createActionCommandAndPassParameters("currentUriFragment", ActionCommandWithWrongParameterCountForAllValuesSetter.class);
    }

    public static class ActionCommandForSettingParametersAndUriFragment implements UriActionCommand {
        public ParameterValue<String> nameValue;
        public String currentUriFragment;
        public CapturedParameterValues allValues;

        @Override
        public void run() {
        }

        @AllCapturedParameters
        public void setAllValues(CapturedParameterValues allValues) {
            this.allValues = allValues;
        }

        @CapturedParameter(mapperName = "mapper", parameterName = "nameParam")
        public void setNameValue(ParameterValue<String> nameValue) {
            this.nameValue = nameValue;
        }

        @CurrentUriFragment
        public void setCurrentUriFragment(String currentUriFragment) {
            this.currentUriFragment = currentUriFragment;
        }
    }

    public static class ActionCommandWithoutAnySetters implements UriActionCommand {
        @Override
        public void run() {
        }
    }

    public static class ActionCommandWithoutDefaultConstructor implements UriActionCommand {
        public ActionCommandWithoutDefaultConstructor(String dummy) {
        }

        @Override
        public void run() {
        }
    }

    private static class ActionCommandWithPrivateVisibility implements UriActionCommand {
        @Override
        public void run() {
        }
    }

    public static class ActionCommandWithPrivateUriFragmentSetter implements UriActionCommand {
        @CurrentUriFragment
        private void setUriFragment(String uriFragment) {
        }

        @Override
        public void run() {
        }
    }

    public static class InheritedActionCommand extends ActionCommandForSettingParametersAndUriFragment {
        public ParameterValue<Integer> integerValue;

        @CapturedParameter(mapperName = "mapper", parameterName = "intParam")
        public void setIntegerValue(ParameterValue<Integer> integerValue) {
            this.integerValue = integerValue;
        }
    }

    public static class ActionCommandWithWrongParameterTypeForParameterSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @CapturedParameter(mapperName = "mapper", parameterName = "intParam")
        public void setIntegerValue(Integer integerValue) {
        }
    }

    public static class ActionCommandWithWrongParameterCountForParameterSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @CapturedParameter(mapperName = "mapper", parameterName = "intParam")
        public void setIntegerValue(ParameterValue<Integer> integerValue, String text) {
        }
    }

    public static class ActionCommandWithWrongParameterTypeForUriFragmentSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @CurrentUriFragment
        public void setUriFragment(Integer integerValue) {
        }
    }

    public static class ActionCommandWithWrongParameterCountForUriFragmentSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @CurrentUriFragment
        public void setUriFragment(String uri, String fragment) {
        }
    }

    public static class ActionCommandWithWrongParameterTypeForAllValuesSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @AllCapturedParameters
        public void setAllParameters(Integer integerValue) {
        }
    }

    public static class ActionCommandWithWrongParameterCountForAllValuesSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @AllCapturedParameters
        public void setAllParameters(CapturedParameterValues allValues, String text) {
        }
    }
}