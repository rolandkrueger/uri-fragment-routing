package org.roklib.urifragmentrouting.helper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.annotation.*;
import org.roklib.urifragmentrouting.exception.InvalidMethodSignatureException;
import org.roklib.urifragmentrouting.mapper.ImmutableActionMapperWrapper;
import org.roklib.urifragmentrouting.mapper.SimpleUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.SingleIntegerUriParameter;
import org.roklib.urifragmentrouting.parameter.SingleStringUriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ActionCommandConfigurerTest {

    private ActionCommandConfigurer factory;
    private CapturedParameterValues capturedParameterValues;
    private SingleStringUriParameter nameParameter;
    private SingleIntegerUriParameter intParameter;
    private TestRoutingContext context;

    @Before
    public void setUp() throws Exception {
        capturedParameterValues = new CapturedParameterValues();
        nameParameter = new SingleStringUriParameter("nameParam");
        intParameter = new SingleIntegerUriParameter("intParam");
        context = new TestRoutingContext();
    }

    @Test
    public void new_instance_of_action_command_is_created() {
        final UriActionCommand action = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> action);

        assertThat(action, instanceOf(ActionCommandForSettingAnyData.class));
    }

    @Test
    public void set_one_captured_parameter() {
        ActionCommandForSettingAnyData result = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> result);
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        factory.passCapturedParameters(capturedParameterValues);
        ActionCommandForSettingAnyData action = (ActionCommandForSettingAnyData) result;
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
    }

    @Test
    public void no_parameters_available() {
        ActionCommandForSettingAnyData result = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passAllCapturedParameters(capturedParameterValues);
        ActionCommandForSettingAnyData action = result;
        assertThat(action.allValues.isEmpty(), is(true));
        assertThat(action.nameValue, is(nullValue()));
    }

    @Test
    public void set_all_captured_parameters() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        ActionCommandForSettingAnyData result = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passAllCapturedParameters(capturedParameterValues);
        ActionCommandForSettingAnyData action = (ActionCommandForSettingAnyData) result;
        assertThat(action.allValues.isEmpty(), is(false));
        assertThat(action.allValues.getValueFor("mapper", "nameParam").getValue(), is("name"));
        assertThat(action.allValues.getValueFor("mapper", "intParam").getValue(), is(17));
    }

    @Test
    public void inherited_setter_methods_will_be_invoked() {
        capturedParameterValues.setValueFor("mapper", nameParameter, ParameterValue.forValue("name"));
        capturedParameterValues.setValueFor("mapper", intParameter, ParameterValue.forValue(17));
        InheritedActionCommand result = new InheritedActionCommand();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passUriFragment("currentUriFragment");
        factory.passAllCapturedParameters(capturedParameterValues);
        factory.passCapturedParameters(capturedParameterValues);
        InheritedActionCommand action = (InheritedActionCommand) result;
        assertThat(action.currentUriFragment, is(equalTo("currentUriFragment")));
        assertThat(action.nameValue.getValue(), is(equalTo("name")));
        assertThat(action.integerValue.getValue(), is(equalTo(17)));
    }

    @Test
    public void set_current_uri_fragment() {
        ActionCommandForSettingAnyData result = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passUriFragment("currentUriFragment");
        assertThat(((ActionCommandForSettingAnyData) result).currentUriFragment, is(equalTo("currentUriFragment")));
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_current_uri_fragment_is_private() {
        ActionCommandWithPrivateSetters result = new ActionCommandWithPrivateSetters();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passUriFragment("currentUriFragment");
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_type() {
        ActionCommandWithWrongParameterTypeForParameterSetter result = new ActionCommandWithWrongParameterTypeForParameterSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passCapturedParameters(capturedParameterValues);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void parameter_setter_has_incorrect_parameter_count() {
        ActionCommandWithWrongParameterCountForParameterSetter result = new ActionCommandWithWrongParameterCountForParameterSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passCapturedParameters(capturedParameterValues);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_type() {
        ActionCommandWithWrongParameterTypeForUriFragmentSetter result = new ActionCommandWithWrongParameterTypeForUriFragmentSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passUriFragment("currentUriFragment");
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void uri_fragment_setter_has_incorrect_parameter_count() {
        ActionCommandWithWrongParameterCountForUriFragmentSetter result = new ActionCommandWithWrongParameterCountForUriFragmentSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passUriFragment("currentUriFragment");
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_type() {
        ActionCommandWithWrongParameterTypeForAllValuesSetter result = new ActionCommandWithWrongParameterTypeForAllValuesSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passAllCapturedParameters(capturedParameterValues);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void all_values_setter_has_incorrect_parameter_count() {
        ActionCommandWithWrongParameterCountForAllValuesSetter result = new ActionCommandWithWrongParameterCountForAllValuesSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passAllCapturedParameters(capturedParameterValues);
    }

    @Test
    public void set_routing_context() {
        ActionCommandForSettingAnyData result = new ActionCommandForSettingAnyData();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passRoutingContext(context);
        assertThat(result.context, is(this.context));
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_routing_context_is_private() {
        ActionCommandWithPrivateSetters result = new ActionCommandWithPrivateSetters();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passRoutingContext(new TestRoutingContext());
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_routing_context_has_incorrect_parameter_type() {
        ActionCommandWithWrongParameterTypeForRoutingContextSetter result = new ActionCommandWithWrongParameterTypeForRoutingContextSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passRoutingContext(context);
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_routing_context_has_incorrect_parameter_count() {
        ActionCommandWithWrongParameterCountForRoutingContextSetter result = new ActionCommandWithWrongParameterCountForRoutingContextSetter();
        factory = new ActionCommandConfigurer(() -> result);
        factory.passRoutingContext(context);
    }

    @Test
    public void set_current_action_mapper() throws Exception {
        SimpleUriPathSegmentActionMapper mapper = new SimpleUriPathSegmentActionMapper("mapper");
        factory = new ActionCommandConfigurer(() -> new UriActionCommand() {
            private UriPathSegmentActionMapper mapper;

            @Override
            public void run() {
                assertThat(mapper.getMapperName(), is("mapper"));
            }

            @CurrentActionMapper
            public void setActionMapper(UriPathSegmentActionMapper mapper) {
                this.mapper = mapper;
                assertTrue(mapper instanceof ImmutableActionMapperWrapper);
            }
        }, mapper);
        factory.passUriPathSegmentActionMapper();
        UriActionCommand command = factory.createUriActionCommand();
        command.run();
    }

    @Test
    public void set_current_action_mapper_with_two_setters() throws Exception {
        SimpleUriPathSegmentActionMapper mapper = new SimpleUriPathSegmentActionMapper("mapper");
        factory = new ActionCommandConfigurer(() -> new UriActionCommand() {
            private UriPathSegmentActionMapper mapperOne;
            private UriPathSegmentActionMapper mapperTwo;

            @Override
            public void run() {
                assertThat(mapperOne.getMapperName(), is(mapper.getMapperName()));
                assertThat(mapperTwo.getMapperName(), is(mapper.getMapperName()));
            }

            @CurrentActionMapper
            public void setActionMapperOne(UriPathSegmentActionMapper mapper) {
                this.mapperOne = mapper;
            }

            @CurrentActionMapper
            public void setActionMapperTwo(UriPathSegmentActionMapper mapper) {
                this.mapperTwo = mapper;
            }
        }, mapper);
        factory.passUriPathSegmentActionMapper();
        UriActionCommand command = factory.createUriActionCommand();
        command.run();
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_action_mapper_is_private() throws Exception {
        factory = new ActionCommandConfigurer(() -> new UriActionCommand() {
            @Override
            public void run() {
            }

            @CurrentActionMapper
            private void setActionMapper(UriPathSegmentActionMapper mapper) {
            }
        }, new SimpleUriPathSegmentActionMapper("mapper"));
        factory.passUriPathSegmentActionMapper();
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_action_mapper_has_incorrect_parameter_type() throws Exception {
        factory = new ActionCommandConfigurer(() -> new UriActionCommand() {
            @Override
            public void run() {
            }

            @CurrentActionMapper
            public void setActionMapper(String mapper) {
            }
        }, new SimpleUriPathSegmentActionMapper("mapper"));
        factory.passUriPathSegmentActionMapper();
    }

    @Test(expected = InvalidMethodSignatureException.class)
    public void setter_for_action_mapper_has_incorrect_parameter_count() {
        factory = new ActionCommandConfigurer(() -> new UriActionCommand() {
            @Override
            public void run() {
            }

            @CurrentActionMapper
            public void setActionMapper(UriPathSegmentActionMapper mapper1, UriPathSegmentActionMapper mapper2) {
            }
        }, new SimpleUriPathSegmentActionMapper("mapper"));
        factory.passUriPathSegmentActionMapper();
    }

    public static class ActionCommandForSettingAnyData implements UriActionCommand {
        public ParameterValue<String> nameValue;
        public String currentUriFragment;
        public CapturedParameterValues allValues;
        public TestRoutingContext context;

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

        @RoutingContext
        public void setRoutingContext(TestRoutingContext context) {
            this.context = context;
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

    public static class ActionCommandWithPrivateSetters implements UriActionCommand {
        @CurrentUriFragment
        private void setUriFragment(String uriFragment) {
        }

        @RoutingContext
        private void setRoutingContext(TestRoutingContext context) {
        }

        @Override
        public void run() {
        }
    }

    public static class InheritedActionCommand extends ActionCommandForSettingAnyData {
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

    public static class ActionCommandWithWrongParameterTypeForRoutingContextSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @RoutingContext
        public void setRoutingContext(String context) {
        }
    }

    public static class ActionCommandWithWrongParameterCountForRoutingContextSetter implements UriActionCommand {
        @Override
        public void run() {
        }

        @RoutingContext
        public void setRoutingContext(TestRoutingContext context, String text) {
        }
    }

    private static class TestRoutingContext {
    }

}