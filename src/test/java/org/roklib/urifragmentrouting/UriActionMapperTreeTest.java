package org.roklib.urifragmentrouting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.CurrentUriFragment;
import org.roklib.urifragmentrouting.annotation.RoutingContext;
import org.roklib.urifragmentrouting.mapper.*;
import org.roklib.urifragmentrouting.parameter.Point2DUriParameter;
import org.roklib.urifragmentrouting.parameter.SingleStringUriParameter;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;
import static org.roklib.urifragmentrouting.parameter.ParameterMode.*;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeTest {

    private final static Logger LOG = LoggerFactory.getLogger(UriActionMapperTreeTest.class);

    private UriActionMapperTree mapperTree;
    private MyRoutingContext context;
    private CapturedParameterValues parameterValues;

    @Before
    public void setUp() {
        context = new MyRoutingContext();
        parameterValues = new CapturedParameterValues();
    }

    @After
    public void printMapperTree() {
        if (mapperTree != null) {
            LOG.info("Using following mapper tree:");
            mapperTree.getMapperOverview().forEach(LOG::info);
            LOG.info("----------------------------------------");
        }
    }

    /**
     * Maps a single path element on a URI fragment action.
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!home</tt>
     */
    @Test
    public void map_single_path_element_on_action() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("home").onActionFactory(MyActionCommand::new)
                .finishMapper(mappers::put).build();

        final String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    @Test
    public void map_single_path_element_on_action_factory() throws Exception {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("home").onActionFactory(MyActionCommand::new)
                .finishMapper(mappers::put).build();

        final String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    @Test
    public void use_configurable_action_factories() throws Exception {
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("home").onActionFactory(() -> new DefaultActionCommand("navigate to home")).finishMapper()
                .map("profile").onActionFactory(() -> new DefaultActionCommand("navigate to profile")).finishMapper()
                .build();

        DefaultActionCommand action = (DefaultActionCommand) mapperTree.interpretFragment("home");
        assertThat(action.data, is("navigate to home"));
        action = (DefaultActionCommand) mapperTree.interpretFragment("profile");
        assertThat(action.data, is("navigate to profile"));
    }

    /**
     * Test that {@link UriActionMapperTree#interpretFragment(String, Object, boolean)} where the last parameter is
     * {@code false} will find the correct URI action command but will not execute it.
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!home</tt>
     */
    @Test
    public void test_interpretFragment_when_action_command_is_not_to_be_executed_right_away() throws Exception {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("home").onActionFactory(MyActionCommand::new)
                .finishMapper(mappers::put).build();

        final String fragment = assembleFragmentToBeInterpreted(mappers);
        final UriActionCommand uriActionCommand = mapperTree.interpretFragment(fragment, context, false);
        assertThat((MyActionCommand) uriActionCommand, isA(MyActionCommand.class));
        assertThat(context.wasMyActionCommandExecuted, is(false));
        assertThat(context.wasDefaultCommandExecuted, is(false));
    }

    /**
     * Maps a nested path element on a URI fragment action.
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!users/profile</tt>
     */
    @Test
    public void map_nested_path_element_on_action() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("users").onSubtree()
                .map("profile").onActionFactory(MyActionCommand::new).finishMapper(mappers::put)
                .finishMapper()
                .build();
        // @formatter:on

        final String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    @Test
    public void map_dispatching_action_mapper_on_action_factory() throws Exception {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("users", mappers::put).onActionFactory(MyActionCommand::new)
                    .onSubtree()
                        .map("profile").onActionFactory(MyActionCommand::new).finishMapper()
                    .finishMapper()
                    .build();
        // @formatter:on
        final String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#DIRECTORY_WITH_NAMES}
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!profile/userId/17</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_directory_with_names_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(DIRECTORY_WITH_NAMES).buildMapperTree()
                .map("profile").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("userId").forType(Long.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "userId", ParameterValue.forValue(17L));

        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("profile", "userId").getValue(), is(17L));
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#DIRECTORY}
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!profile/john.doe</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_directory_without_names_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(DIRECTORY).buildMapperTree()
                .map("profile").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("userName").forType(String.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "userName", ParameterValue.forValue("john.doe"));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("profile", "userName").getValue(), is("john.doe"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creating_single_valued_parameter_with_unsupported_type_throws_exception() throws Exception {
        mapperTree = UriActionMapperTree.create().useParameterMode(DIRECTORY).buildMapperTree()
                .map("profile").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("userName").forType(Runnable.class /* class Runnable cannot be used as a parameter's type */).noDefault()
                .finishMapper()
                .build();
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#QUERY}
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!profile?admin=true</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_query_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(QUERY).buildMapperTree()
                .map("profile").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("admin").forType(Boolean.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "admin", ParameterValue.forValue(Boolean.TRUE));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("profile", "admin").getValue(), is(Boolean.TRUE));
    }

    /**
     * Use a single-valued parameter that is defined with a default value. It the interpreted URI fragment does not
     * contain a value for this parameter, the default value is used.
     */
    @Test
    public void use_parameter_with_default_value() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("showData").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("mode").forType(String.class).usingDefaultValue("full")
                .finishMapper(mappers::put)
                .build();

        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        final ParameterValue<Object> value = context.capturedValues.getValueFor("showData", "mode");
        assertThat(value.getValue(), is("full"));
        assertThat(value.isDefaultValue(), is(true));
    }

    /**
     * Use a parameter object that consists of more than one single values.
     */
    @Test
    public void use_multi_valued_parameter() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        final Point2DUriParameter coordinateParameter = new Point2DUriParameter("coordinates", "lon", "lat");

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("location").onActionFactory(MyActionCommand::new)
                .withParameter(coordinateParameter)
                .finishMapper(mappers::put)
                .build();

        final Point2D.Double location = new Point2D.Double(17.0, 42.0);
        parameterValues.setValueFor("location", "coordinates", ParameterValue.forValue(location));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("location", "coordinates").getValue(), is(location));
    }

    @Test
    public void use_preconfigured_parameter() throws Exception {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        final Point2DUriParameter coordinateParameter = new Point2DUriParameter("coordinates", "lon", "lat");

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("productLocation")
                .withParameter(coordinateParameter)
                .onSubtree()
                .map("details").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("mode").forType(String.class).usingDefaultValue("full")
                .finishMapper(mappers::put)
                .finishMapper()
                .build();
        // @formatter:on

        final Point2D.Double location = new Point2D.Double(17.0, 42.0);
        parameterValues.setValueFor("productLocation", "coordinates", ParameterValue.forValue(location));
        parameterValues.setValueFor("details", "mode", ParameterValue.forValue("summary"));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("productLocation", "coordinates").getValue(), is(location));
        assertThat(context.capturedValues.getValueFor("details", "mode").getValue(), is("summary"));
    }

    /**
     * Define a default value for a multi-valued parameter.
     */
    @Test
    public void use_multi_valued_parameter_with_default_value() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        final Point2DUriParameter coordinateParameter = new Point2DUriParameter("coordinates", "lon", "lat");
        final Point2D.Double origin = new Point2D.Double(0, 0);
        coordinateParameter.setOptional(origin);

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("location").onActionFactory(MyActionCommand::new)
                .withParameter(coordinateParameter)
                .finishMapper(mappers::put)
                .build();

        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        final ParameterValue<Object> value = context.capturedValues.getValueFor("location", "coordinates");
        assertThat(value.getValue(), is(origin));
        assertThat(value.isDefaultValue(), is(true));
    }

    @Test
    public void use_parameters_for_inner_path_segments() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("products")
                .withSingleValuedParameter("id").forType(Long.class).noDefault()
                .onSubtree()
                .map("details").onActionFactory(MyActionCommand::new)
                .withSingleValuedParameter("mode").forType(String.class).usingDefaultValue("full")
                .finishMapper(mappers::put)
                .finishMapper()
                .build();
        // @formatter:on

        parameterValues.setValueFor("products", "id", ParameterValue.forValue(17L));
        parameterValues.setValueFor("details", "mode", ParameterValue.forValue("summary"));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("products", "id").getValue(), is(17L));
        assertThat(context.capturedValues.getValueFor("details", "mode").getValue(), is("summary"));
    }

    /**
     * Define a default action command which is executed for each interpreted URI fragment for which no action command
     * could be resolved.
     */
    @Test
    public void use_a_default_action_command() {
        // @formatter:off
        mapperTree = UriActionMapperTree.create()
                .useDefaultActionCommandFactory(DefaultActionCommand::new)
                .buildMapperTree()
                .mapSubtree("show").onSubtree()
                .map("users").onActionFactory(MyActionCommand::new).finishMapper()
                .finishMapper()
                .build();
        // @formatter:on
        interpretFragment("show");
        assertThat(context.wasDefaultCommandExecuted, is(true));
    }

    /**
     * Use a custom mapper object which is not created by the {@link UriActionMapperTree.UriActionMapperTreeBuilder} but
     * is instead provided by the developer. This is useful if you're writing your own sub-class of {@link
     * AbstractUriPathSegmentActionMapper} and want to insert an object of this custom mapper into a mapper tree.
     * <p>
     * You can use custom mappers with {@link UriActionMapperTree.MapperTreeBuilder#mapSubtree(org.roklib.urifragmentrouting.mapper.DispatchingUriPathSegmentActionMapper)}
     * and with {@link }
     * <p>
     * Example URL for this case: <tt>http://www.example.com#!custom</tt>
     */
    @Test
    public void use_a_custom_defined_mapper() {
        final SimpleUriPathSegmentActionMapper customMapper = new SimpleUriPathSegmentActionMapper("custom");
        customMapper.setActionCommandFactory(MyActionCommand::new);

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .addMapper(customMapper)
                .build();

        final String fragment = mapperTree.assembleUriFragment(customMapper);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    /**
     * Use mapper class {@link RegexUriPathSegmentActionMapper} to interpret a segment of URI fragments with a regular
     * expression.
     * <p>
     * This example additionally shows how to provide a custom dispatching mapper object which is a {@link
     * RegexUriPathSegmentActionMapper} in this case.
     */
    @Test
    public void use_regex_dispatching_mapper() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        final AbstractRegexToStringListParameterValueConverter regexConverter =
                new AbstractRegexToStringListParameterValueConverter("(\\d+)_\\w+") {
                    @Override
                    public String convertToString(final List<String> value) {
                        return value.get(0) + "_" + value.get(1);
                    }
                };

        final RegexUriPathSegmentActionMapper regexMapper =
                new RegexUriPathSegmentActionMapper("regexMapper", "regexParameter", regexConverter);

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("blog").onSubtree()
                .mapSubtree(regexMapper).onSubtree()
                .map("view").onActionFactory(MyActionCommand::new).finishMapper(mappers::put)
                .finishMapper()
                .finishMapper()
                .build();
        // @formatter:on

        parameterValues.setValueFor("regexMapper", "regexParameter",
                ParameterValue.forValue(Arrays.asList("1723", "how_to_foo_a_bar")));
        final String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("regexMapper", "regexParameter").getValue(), is(Collections.singletonList("1723")));
    }

    @Test
    public void use_catch_all_dispatching_mapper() {
        final CatchAllUriPathSegmentActionMapper<String> catchAllMapper = new CatchAllUriPathSegmentActionMapper<>("catchAll", new SingleStringUriParameter("param"));
        catchAllMapper.setActionCommandFactory(MyActionCommand::new);

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                .addMapper(catchAllMapper)
                .map("segment").onActionFactory(DefaultActionCommand::new).finishMapper()
                .build();
        // @formatter:on
        interpretFragment("/root/segment");
        assertThatDefaultActionCommandWasExecuted();
        context = new MyRoutingContext();

        interpretFragment("/root/arbitrary_stuff");
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("catchAll", "param").getValue(), is("arbitrary_stuff"));
    }

    @Test
    public void catch_all_dispatching_mapper_is_always_used_last() {
        final CatchAllUriPathSegmentActionMapper<String> catchAllMapper = new CatchAllUriPathSegmentActionMapper<>("catchAll", new SingleStringUriParameter("param"));

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                .mapSubtree(catchAllMapper).onSubtree().map("sub").onActionFactory(MyActionCommand::new).finishMapper()
                .finishMapper()
                .map("segment").onActionFactory(DefaultActionCommand::new).finishMapper()
                .build();
        // @formatter:on

        interpretFragment("/root/segment");
        assertThatDefaultActionCommandWasExecuted();
        context = new MyRoutingContext();

        interpretFragment("/root/handled_by_catch_all_mapper/sub");
        assertThatMyActionCommandWasExecuted();
    }

    @Test
    public void only_one_catch_all_dispatching_mapper_can_be_active_per_parent_mapper() {
        final CatchAllUriPathSegmentActionMapper<String> catchAllMapper1 = new CatchAllUriPathSegmentActionMapper<>("catchAll1", new SingleStringUriParameter("param"));
        final CatchAllUriPathSegmentActionMapper<String> catchAllMapper2 = new CatchAllUriPathSegmentActionMapper<>("catchAll2", new SingleStringUriParameter("param"));

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                .mapSubtree(catchAllMapper1).onSubtree().map("sub1").onPathSegment("sub").onActionFactory(MyActionCommand::new).finishMapper()
                .finishMapper()
                .mapSubtree(catchAllMapper2).onSubtree().map("sub2").onPathSegment("sub").onActionFactory(DefaultActionCommand::new).finishMapper().finishMapper()
                .build();
        // @formatter:on

        interpretFragment("/root/any/sub");
        assertThatDefaultActionCommandWasExecuted();
    }

    @Test
    public void use_starts_with_dispatching_mapper() {
        final StartsWithUriPathSegmentActionMapper startsWithMapper = new StartsWithUriPathSegmentActionMapper("blogPostId", "id_", "blogId");

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree(startsWithMapper)
                .onActionFactory(MyActionCommand::new)
                .onSubtree()
                .finishMapper()
                .build();

        parameterValues.setValueFor("blogPostId", "blogId", ParameterValue.forValue(Collections.singletonList("95829")));
        final String fragment = mapperTree.assembleUriFragment(parameterValues, startsWithMapper);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void using_the_same_mapper_name_more_than_once_is_not_allowed() {
        UriActionMapperTree.create().buildMapperTree()
                .map("segment").onActionFactory(MyActionCommand::new).finishMapper()
                .map("segment").onActionFactory(MyActionCommand::new).finishMapper()
                .build();
    }

    @Test
    public void use_the_same_path_segment_name_twice() {
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("mapper_1").onPathSegment("segment").onActionFactory(DefaultActionCommand::new).finishMapper()
                .mapSubtree("sub").onSubtree()
                .map("mapper_2").onPathSegment("segment").onActionFactory(MyActionCommand::new).finishMapper()
                .build();
        // @formatter:on

        interpretFragment("/segment");
        assertThatDefaultActionCommandWasExecuted();

        context = new MyRoutingContext();
        interpretFragment("/sub/segment");
        assertThatMyActionCommandWasExecuted();
    }


    @Test
    public void use_the_same_path_segment_name_twice_and_assemble_uri_fragment() {
        final UriPathSegmentActionMapper[] mappers = new UriPathSegmentActionMapper[1];
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("mapper_1").onPathSegment("segment").onActionFactory(DefaultActionCommand::new).finishMapper()
                .mapSubtree("sub").onSubtree()
                .map("mapper_2").onPathSegment("segment").onActionFactory(MyActionCommand::new).finishMapper(mapper -> mappers[0] = mapper)
                .build();
        // @formatter:on

        final String uriFragment = mapperTree.assembleUriFragment(mappers[0]);
        assertThat(uriFragment, is("sub/segment"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void using_the_same_mapper_name_more_than_once_in_a_subtree_is_not_allowed() {
        // @formatter:off
        UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("segment").onSubtree()
                .mapSubtree("test").onSubtree()
                .mapSubtree("segment").onSubtree().finishMapper()
                .build();
        // @formatter:on
    }

    @Test
    public void use_the_same_path_segment_name_twice_for_dispatching_mappers() {
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("firstPath").onSubtree()
                .mapSubtree("subTreeMapper_1", "segment").onSubtree()
                .map("action1").onPathSegment("action").onActionFactory(DefaultActionCommand::new).finishMapper()
                .finishMapper()
                .finishMapper()
                .mapSubtree("secondPath").onSubtree()
                .mapSubtree("subTreeMapper_2", "segment").onSubtree()
                .map("action2").onPathSegment("action").onActionFactory(MyActionCommand::new).finishMapper()
                .build();
        // @formatter:on

        interpretFragment("/firstPath/segment/action");
        assertThatDefaultActionCommandWasExecuted();

        context = new MyRoutingContext();
        interpretFragment("/secondPath/segment/action");
        assertThatMyActionCommandWasExecuted();
    }

    private void assertThatMyActionCommandWasExecuted() {
        assertThat(context.wasMyActionCommandExecuted, is(true));
        assertThat(context.wasDefaultCommandExecuted, is(false));
    }

    private void assertThatDefaultActionCommandWasExecuted() {
        assertThat(context.wasDefaultCommandExecuted, is(true));
        assertThat(context.wasMyActionCommandExecuted, is(false));
    }

    private String assembleFragmentToBeInterpreted(final MapperObjectContainer mappers) {
        return assembleFragmentToBeInterpreted(mappers, null, 0);
    }

    private String assembleFragmentToBeInterpreted(final MapperObjectContainer mappers, final CapturedParameterValues parameterValues, final int forMapperAtIndex) {
        return mapperTree.assembleUriFragment(parameterValues, mappers.get(forMapperAtIndex));
    }

    private void interpretFragment(final String fragment) {
        mapperTree.interpretFragment(fragment, context);
    }

    public static class MyActionCommand implements UriActionCommand {
        protected MyRoutingContext context;

        @Override
        public void run() {
            if (context != null) {
                context.wasMyActionCommandExecuted = true;
            }
        }

        @RoutingContext
        public void setContext(final MyRoutingContext context) {
            this.context = context;
        }

        @CurrentUriFragment
        public void setCurrentUriFragment(final String currentUriFragment) {
            LOG.info("Interpreting fragment: '" + currentUriFragment + "'");
        }

        @AllCapturedParameters
        public void setCapturedValues(final CapturedParameterValues values) {
            LOG.info("Setting captured parameter values: {}", values);
            if (context != null) {
                context.capturedValues = values;
            }
        }

        @Override
        public String toString() {
            return getClass().getName();
        }
    }

    public static class DefaultActionCommand extends MyActionCommand {
        public String data;

        public DefaultActionCommand() {
        }

        public DefaultActionCommand(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            if (context != null) {
                context.wasDefaultCommandExecuted = true;
            }
        }

    }

    public static class MyRoutingContext {
        public boolean wasMyActionCommandExecuted = false;
        public boolean wasDefaultCommandExecuted = false;
        public CapturedParameterValues capturedValues;

        @Override
        public String toString() {
            return "{MyRoutingContext - Context for unit tests}";
        }
    }

    private static class MapperObjectContainer {
        private UriPathSegmentActionMapper[] mappers = new UriPathSegmentActionMapper[1];

        public MapperObjectContainer() {
            this(1);
        }

        public void put(final UriPathSegmentActionMapper mapper, final int index) {
            mappers[index] = mapper;
        }

        public void put(final UriPathSegmentActionMapper mapper) {
            put(mapper, 0);
        }

        public MapperObjectContainer(final int numberOfMappers) {
            this.mappers = new UriPathSegmentActionMapper[numberOfMappers];
        }

        public UriPathSegmentActionMapper get(final int index) {
            return mappers[index];
        }
    }

}