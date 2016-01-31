package org.roklib.webapps.uridispatching.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.exception.InvalidActionCommandClassException;
import org.roklib.webapps.uridispatching.exception.InvalidMethodSignatureException;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;
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
    private SingleStringURIParameter nameParameter;
    private SingleIntegerURIParameter intParameter;

    @Before
    public void setUp() throws Exception {
        capturedParameterValues = new CapturedParameterValuesImpl();
        nameParameter = new SingleStringURIParameter("nameParam");
        intParameter = new SingleIntegerURIParameter("intParam");
    }

    @Test
    public void new_instance_of_action_command_is_created() {
        final URIActionCommand action = capturedParameterValues.passParametersToActionCommand(null,
                ActionCommandForSettingParametersAndUriFragment.class);

        assertThat(action, instanceOf(ActionCommandForSettingParametersAndUriFragment.class));
    }

    @Test
    public void set_one_captured_parameter() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        final URIActionCommand result = capturedParameterValues.passParametersToActionCommand("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
    }

    @Test
    public void no_parameters_available() {
        final URIActionCommand result = capturedParameterValues.passParametersToActionCommand("currentUriFragment",
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
        final URIActionCommand result = capturedParameterValues.passParametersToActionCommand("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.allValues.isEmpty(), is(false));
        assertThat(action.allValues.getValueFor("mapper", "nameParam").get().getValue(), is("name"));
        assertThat(action.allValues.getValueFor("mapper", "intParam").get().getValue(), is(17));
    }

    @Test
    public void inherited_setter_methods_will_be_invoked() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        final URIActionCommand result = capturedParameterValues.passParametersToActionCommand("currentUriFragment",
                InheritedActionCommand.class);
        InheritedActionCommand action = (InheritedActionCommand) result;
        assertThat(action.currentUriFragment, is(equalTo("currentUriFragment")));
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
        assertThat(action.integerValue.getValue(), is(equalTo(17)));
    }

    @Test
    public void set_current_uri_fragment() {
        final URIActionCommand action = capturedParameterValues.passParametersToActionCommand("currentUriFragment",
                ActionCommandForSettingParametersAndUriFragment.class);

        assertThat(((ActionCommandForSettingParametersAndUriFragment) action).currentUriFragment, is(equalTo("currentUriFragment")));
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_with_private_visibility() {
        capturedParameterValues.passParametersToActionCommand(null, ActionCommandWithPrivateVisibility.class);
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_without_default_constructor() {
        capturedParameterValues.passParametersToActionCommand(null, ActionCommandWithoutDefaultConstructor.class);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_current_uri_fragment_is_private() {
        capturedParameterValues.passParametersToActionCommand("currentUriFragment", ActionCommandWithPrivateUriFragmentSetter.class);
    }

    @Test
    public void use_action_command_without_any_setters() {
        final URIActionCommand result = capturedParameterValues.passParametersToActionCommand("currentUriFragment", ActionCommandWithoutAnySetters.class);
        assertThat(result, is(instanceOf(ActionCommandWithoutAnySetters.class)));
    }

    public static class ActionCommandForSettingParametersAndUriFragment implements URIActionCommand {
        public ParameterValue<String> nameValue;
        public String currentUriFragment;
        public CapturedParameterValues allValues;

        @Override
        public void execute() {
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

    public static class ActionCommandWithoutAnySetters implements URIActionCommand {
        @Override
        public void execute() {
        }
    }


    public static class ActionCommandWithoutDefaultConstructor implements URIActionCommand {
        public ActionCommandWithoutDefaultConstructor(String dummy) {
        }

        @Override
        public void execute() {
        }
    }

    private static class ActionCommandWithPrivateVisibility implements URIActionCommand {
        @Override
        public void execute() {
        }
    }

    public static class ActionCommandWithPrivateUriFragmentSetter implements URIActionCommand {
        @CurrentUriFragment
        private void setUriFragment(String uriFragment) {
        }

        @Override
        public void execute() {
        }
    }

    public static class InheritedActionCommand extends ActionCommandForSettingParametersAndUriFragment {
        public ParameterValue<Integer> integerValue;

        @CapturedParameter(mapperName = "mapper", parameterName = "intParam")
        public void setIntegerValue(ParameterValue<Integer> integerValue) {
            this.integerValue = integerValue;
        }
    }
}