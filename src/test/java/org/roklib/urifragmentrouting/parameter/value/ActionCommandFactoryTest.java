package org.roklib.urifragmentrouting.parameter.value;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.exception.InvalidActionCommandClassException;
import org.roklib.urifragmentrouting.exception.InvalidMethodSignatureException;
import org.roklib.urifragmentrouting.helper.ActionCommandFactory;
import org.roklib.urifragmentrouting.parameter.SingleIntegerUriParameter;
import org.roklib.urifragmentrouting.parameter.SingleStringUriParameter;
import org.roklib.urifragmentrouting.parameter.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.parameter.annotation.CapturedParameter;
import org.roklib.urifragmentrouting.parameter.annotation.CurrentUriFragment;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class ActionCommandFactoryTest {

    private ActionCommandFactory<TestRoutingContext> factory;
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
        factory = new ActionCommandFactory<>(ActionCommandForSettingParametersAndUriFragment.class);
        final UriActionCommand action = factory.createCommand();

        assertThat(action, instanceOf(ActionCommandForSettingParametersAndUriFragment.class));
    }

    @Test
    public void set_one_captured_parameter() {
        factory = new ActionCommandFactory<>(ActionCommandForSettingParametersAndUriFragment.class);
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        final UriActionCommand result = factory.createCommand();
        factory.passCapturedParameters(capturedParameterValues, ActionCommandForSettingParametersAndUriFragment.class, result);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
    }

    @Test
    public void no_parameters_available() {
        factory = new ActionCommandFactory<>(ActionCommandForSettingParametersAndUriFragment.class);

        final UriActionCommand result = factory.createCommand();
        factory.passAllCapturedParameters(capturedParameterValues, ActionCommandForSettingParametersAndUriFragment.class, result);
        factory.passCapturedParameters(capturedParameterValues, ActionCommandForSettingParametersAndUriFragment.class, result);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.allValues.isEmpty(), is(true));
        assertThat(action.nameValue, is(nullValue()));
    }

    @Test
    public void set_all_captured_parameters() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        factory = new ActionCommandFactory<>(ActionCommandForSettingParametersAndUriFragment.class);
        final UriActionCommand result = factory.createCommand();
        factory.passAllCapturedParameters(capturedParameterValues, ActionCommandForSettingParametersAndUriFragment.class, result);
        ActionCommandForSettingParametersAndUriFragment action = (ActionCommandForSettingParametersAndUriFragment) result;
        assertThat(action.allValues.isEmpty(), is(false));
        assertThat(action.allValues.getValueFor("mapper", "nameParam").getValue(), is("name"));
        assertThat(action.allValues.getValueFor("mapper", "intParam").getValue(), is(17));
    }

    @Test
    public void inherited_setter_methods_will_be_invoked() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        factory = new ActionCommandFactory<>(InheritedActionCommand.class);
        final UriActionCommand result = factory.createCommand();
        factory.passUriFragment("currentUriFragment", InheritedActionCommand.class, result);
        factory.passCapturedParameters(capturedParameterValues, InheritedActionCommand.class, result);
        InheritedActionCommand action = (InheritedActionCommand) result;
        assertThat(action.currentUriFragment, is(equalTo("currentUriFragment")));
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
        assertThat(action.integerValue.getValue(), is(equalTo(17)));
    }

    @Test
    public void set_current_uri_fragment() {
        factory = new ActionCommandFactory<>(ActionCommandForSettingParametersAndUriFragment.class);
        final UriActionCommand result = factory.createCommand();
        factory.passUriFragment("currentUriFragment", ActionCommandForSettingParametersAndUriFragment.class, result);
        assertThat(((ActionCommandForSettingParametersAndUriFragment) result).currentUriFragment, is(equalTo("currentUriFragment")));
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_with_private_visibility() {
        factory = new ActionCommandFactory<>(ActionCommandWithPrivateVisibility.class);
        factory.createCommand();
    }

    @Test(expected = InvalidActionCommandClassException.class)
    public void use_action_command_without_default_constructor() {
        factory = new ActionCommandFactory<>(ActionCommandWithoutDefaultConstructor.class);
        factory.createCommand();
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_current_uri_fragment_is_private() {
        factory = new ActionCommandFactory<>(ActionCommandWithPrivateUriFragmentSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passUriFragment("currentUriFragment", ActionCommandWithPrivateUriFragmentSetter.class, result);
    }

    @Test
    public void use_action_command_without_any_setters() {
        factory = new ActionCommandFactory<>(ActionCommandWithoutAnySetters.class);
        final UriActionCommand result = factory.createCommand();
        assertThat(result, is(instanceOf(ActionCommandWithoutAnySetters.class)));
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_type() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterTypeForParameterSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passCapturedParameters(capturedParameterValues, ActionCommandWithWrongParameterTypeForParameterSetter.class, result);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_count() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterCountForParameterSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passCapturedParameters(capturedParameterValues, ActionCommandWithWrongParameterCountForParameterSetter.class, result);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_type() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterTypeForUriFragmentSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passUriFragment("currentUriFragment", ActionCommandWithWrongParameterTypeForUriFragmentSetter.class, result);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_count() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterCountForUriFragmentSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passUriFragment("currentUriFragment", ActionCommandWithWrongParameterCountForUriFragmentSetter.class, result);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_type() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterTypeForAllValuesSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passAllCapturedParameters(capturedParameterValues, ActionCommandWithWrongParameterTypeForAllValuesSetter.class, result);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_count() {
        factory = new ActionCommandFactory<>(ActionCommandWithWrongParameterCountForAllValuesSetter.class);
        final UriActionCommand result = factory.createCommand();
        factory.passAllCapturedParameters(capturedParameterValues, ActionCommandWithWrongParameterCountForAllValuesSetter.class, result);
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
        // should be ParameterValue<Integer> instead of Integer
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

    private static class TestRoutingContext {
    }

}