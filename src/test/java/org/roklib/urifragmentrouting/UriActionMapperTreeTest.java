package org.roklib.urifragmentrouting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.urifragmentrouting.mapper.*;
import org.roklib.urifragmentrouting.parameter.Point2DUriParameter;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.CurrentUriFragment;
import org.roklib.urifragmentrouting.annotation.RoutingContext;
import org.roklib.urifragmentrouting.parameter.SingleStringUriParameter;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.roklib.urifragmentrouting.parameter.ParameterMode.DIRECTORY;
import static org.roklib.urifragmentrouting.parameter.ParameterMode.DIRECTORY_WITH_NAMES;
import static org.roklib.urifragmentrouting.parameter.ParameterMode.QUERY;

@RunWith(MockitoJUnitRunner.class)
public class UriActionMapperTreeTest {

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
            System.out.println("... with following mapper tree:\n");
            mapperTree.print(System.out);
            System.out.println("----------------------------------------");
        }
    }

    /**
     * Maps a single path element on a URI fragment action.
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!home</tt>
     */
    @Test
    public void map_single_path_element_on_action() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("home").onAction(MyActionCommand.class)
                .finishMapper(mappers::put).build();

        String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    /**
     * Maps a nested path element on a URI fragment action.
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!users/profile</tt>
     */
    @Test
    public void map_nested_path_element_on_action() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("users").onSubtree()
                    .map("profile").onAction(MyActionCommand.class).finishMapper(mappers::put)
                .finishMapper()
                .build();
        // @formatter:on

        String fragment = assembleFragmentToBeInterpreted(mappers);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#DIRECTORY_WITH_NAMES}
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!profile/userId/17</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_directory_with_names_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(DIRECTORY_WITH_NAMES).buildMapperTree()
                .map("profile").onAction(MyActionCommand.class)
                .withSingleValuedParameter("userId").forType(Long.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "userId", ParameterValue.forValue(17L));

        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("profile", "userId").getValue(), is(17L));
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#DIRECTORY}
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!profile/john.doe</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_directory_without_names_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(DIRECTORY).buildMapperTree()
                .map("profile").onAction(MyActionCommand.class)
                .withSingleValuedParameter("userName").forType(String.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "userName", ParameterValue.forValue("john.doe"));
        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("profile", "userName").getValue(), is("john.doe"));
    }

    /**
     * Maps a single path element with a single-valued parameter on a URI fragment action. Parameter mode is {@link
     * org.roklib.urifragmentrouting.parameter.ParameterMode#QUERY}
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!profile?admin=true</tt>
     */
    @Test
    public void map_single_path_element_with_parameter_and_query_parameter_mode() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        mapperTree = UriActionMapperTree.create().useParameterMode(QUERY).buildMapperTree()
                .map("profile").onAction(MyActionCommand.class)
                .withSingleValuedParameter("admin").forType(Boolean.class).noDefault()
                .finishMapper(mappers::put)
                .build();

        parameterValues.setValueFor("profile", "admin", ParameterValue.forValue(Boolean.TRUE));
        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
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
                .map("showData").onAction(MyActionCommand.class)
                .withSingleValuedParameter("mode").forType(String.class).usingDefaultValue("full")
                .finishMapper(mappers::put)
                .build();

        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
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

        Point2DUriParameter coordinateParameter = new Point2DUriParameter("coordinates", "lon", "lat");

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("location").onAction(MyActionCommand.class)
                .withParameter(coordinateParameter)
                .finishMapper(mappers::put)
                .build();

        Point2D.Double location = new Point2D.Double(17.0, 42.0);
        parameterValues.setValueFor("location", "coordinates", ParameterValue.forValue(location));
        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("location", "coordinates").getValue(), is(location));
    }

    /**
     * Define a default value for a multi-valued parameter.
     */
    @Test
    public void use_multi_valued_parameter_with_default_value() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        Point2DUriParameter coordinateParameter = new Point2DUriParameter("coordinates", "lon", "lat");
        Point2D.Double origin = new Point2D.Double(0, 0);
        coordinateParameter.setOptional(origin);

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("location").onAction(MyActionCommand.class)
                .withParameter(coordinateParameter)
                .finishMapper(mappers::put)
                .build();

        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
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
                    .map("details").onAction(MyActionCommand.class)
                    .withSingleValuedParameter("mode").forType(String.class).usingDefaultValue("full")
                    .finishMapper(mappers::put)
                .finishMapper()
                .build();
        // @formatter:on

        parameterValues.setValueFor("products", "id", ParameterValue.forValue(17L));
        parameterValues.setValueFor("details", "mode", ParameterValue.forValue("summary"));
        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
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
                .useDefaultActionCommand(DefaultActionCommand.class)
                .buildMapperTree()
                .mapSubtree("show").onSubtree()
                    .map("users").onAction(MyActionCommand.class).finishMapper()
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
     * <p/>
     * You can use custom mappers with {@link UriActionMapperTree.MapperTreeBuilder#mapSubtree(org.roklib.urifragmentrouting.mapper.DispatchingUriPathSegmentActionMapper)}
     * and with {@link }
     * <p/>
     * Example URL for this case: <tt>http://www.example.com#!custom</tt>
     */
    @Test
    public void use_a_custom_defined_mapper() {
        SimpleUriPathSegmentActionMapper customMapper = new SimpleUriPathSegmentActionMapper("custom");
        customMapper.setActionCommandClass(MyActionCommand.class);

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .addMapper(customMapper)
                .build();

        String fragment = mapperTree.assembleUriFragment(customMapper);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    /**
     * Use mapper class {@link RegexUriPathSegmentActionMapper} to interpret a segment of URI fragments with a regular
     * expression.
     * <p/>
     * This example additionally shows how to provide a custom dispatching mapper object which is a {@link
     * RegexUriPathSegmentActionMapper} in this case.
     */
    @Test
    public void use_regex_dispatching_mapper() {
        final MapperObjectContainer mappers = new MapperObjectContainer();

        AbstractRegexToStringListParameterValueConverter regexConverter =
                new AbstractRegexToStringListParameterValueConverter("(\\d+)_\\w+") {
                    @Override
                    public String convertToString(List<String> value) {
                        return value.get(0) + "_" + value.get(1);
                    }
                };

        RegexUriPathSegmentActionMapper regexMapper =
                new RegexUriPathSegmentActionMapper("regexMapper", "regexParameter", regexConverter);

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("blog").onSubtree()
                    .mapSubtree(regexMapper).onSubtree()
                        .map("view").onAction(MyActionCommand.class).finishMapper(mappers::put)
                    .finishMapper()
                .finishMapper()
                .build();
        // @formatter:on

        parameterValues.setValueFor("regexMapper", "regexParameter",
                ParameterValue.forValue(Arrays.asList("1723", "how_to_foo_a_bar")));
        String fragment = assembleFragmentToBeInterpreted(mappers, parameterValues, 0);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
        assertThat(context.capturedValues.getValueFor("regexMapper", "regexParameter").getValue(), is(Collections.singletonList("1723")));
    }

    @Test
    public void use_catch_all_dispatching_mapper() {
        CatchAllUriPathSegmentActionMapper<String> catchAllMapper = new CatchAllUriPathSegmentActionMapper<>("catchAll", new SingleStringUriParameter("param"));
        catchAllMapper.setActionCommandClass(MyActionCommand.class);

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                    .addMapper(catchAllMapper)
                    .map("segment").onAction(DefaultActionCommand.class).finishMapper()
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
        CatchAllUriPathSegmentActionMapper<String> catchAllMapper = new CatchAllUriPathSegmentActionMapper<>("catchAll", new SingleStringUriParameter("param"));

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                    .mapSubtree(catchAllMapper).onSubtree().map("sub").onAction(MyActionCommand.class).finishMapper()
                    .finishMapper()
                    .map("segment").onAction(DefaultActionCommand.class).finishMapper()
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
        CatchAllUriPathSegmentActionMapper<String> catchAllMapper1 = new CatchAllUriPathSegmentActionMapper<>("catchAll1", new SingleStringUriParameter("param"));
        CatchAllUriPathSegmentActionMapper<String> catchAllMapper2 = new CatchAllUriPathSegmentActionMapper<>("catchAll2", new SingleStringUriParameter("param"));

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("root").onSubtree()
                    .mapSubtree(catchAllMapper1).onSubtree().map("sub1").onPathSegment("sub").onAction(MyActionCommand.class).finishMapper()
                    .finishMapper()
                    .mapSubtree(catchAllMapper2).onSubtree().map("sub2").onPathSegment("sub").onAction(DefaultActionCommand.class).finishMapper().finishMapper()
                .build();
        // @formatter:on

        interpretFragment("/root/any/sub");
        assertThatDefaultActionCommandWasExecuted();
    }

    @Test
    public void use_starts_with_dispatching_mapper() {
        StartsWithUriPathSegmentActionMapper startsWithMapper = new StartsWithUriPathSegmentActionMapper("blogPostId", "id_", "blogId");

        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree(startsWithMapper)
                .onAction(MyActionCommand.class)
                .onSubtree()
                .finishMapper()
                .build();

        parameterValues.setValueFor("blogPostId", "blogId", ParameterValue.forValue(Collections.singletonList("95829")));
        String fragment = mapperTree.assembleUriFragment(parameterValues, startsWithMapper);
        interpretFragment(fragment);
        assertThatMyActionCommandWasExecuted();
    }

    @Test(expected = IllegalArgumentException.class)
    public void using_the_same_mapper_name_more_than_once_is_not_allowed() {
        UriActionMapperTree.create().buildMapperTree()
                .map("segment").onAction(MyActionCommand.class).finishMapper()
                .map("segment").onAction(MyActionCommand.class).finishMapper()
                .build();
    }

    @Test
    public void use_the_same_path_segment_name_twice() {
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("mapper_1").onPathSegment("segment").onAction(DefaultActionCommand.class).finishMapper()
                .mapSubtree("sub").onSubtree()
                    .map("mapper_2").onPathSegment("segment").onAction(MyActionCommand.class).finishMapper()
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
        AbstractUriPathSegmentActionMapper[] mappers = new AbstractUriPathSegmentActionMapper[1];
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("mapper_1").onPathSegment("segment").onAction(DefaultActionCommand.class).finishMapper()
                .mapSubtree("sub").onSubtree()
                    .map("mapper_2").onPathSegment("segment").onAction(MyActionCommand.class).finishMapper(mapper -> mappers[0] = mapper)
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
                        .map("action1").onPathSegment("action").onAction(DefaultActionCommand.class).finishMapper()
                    .finishMapper()
                .finishMapper()
                .mapSubtree("secondPath").onSubtree()
                    .mapSubtree("subTreeMapper_2", "segment").onSubtree()
                        .map("action2").onPathSegment("action").onAction(MyActionCommand.class).finishMapper()
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

    private String assembleFragmentToBeInterpreted(MapperObjectContainer mappers) {
        return assembleFragmentToBeInterpreted(mappers, null, 0);
    }

    private String assembleFragmentToBeInterpreted(MapperObjectContainer mappers, CapturedParameterValues parameterValues, int forMapperAtIndex) {
        return mapperTree.assembleUriFragment(parameterValues, mappers.get(forMapperAtIndex));
    }

    private void interpretFragment(String fragment) {
        mapperTree.interpretFragment(fragment, context);
    }

    public static class MyActionCommand implements UriActionCommand {
        protected MyRoutingContext context;

        @Override
        public void run() {
            context.wasMyActionCommandExecuted = true;
        }

        @RoutingContext
        public void setContext(MyRoutingContext context) {
            this.context = context;
        }

        @CurrentUriFragment
        public void setCurrentUriFragment(String currentUriFragment) {
            System.out.println("Interpreting fragment: '" + currentUriFragment + "'");
        }

        @AllCapturedParameters
        public void setCapturedValues(CapturedParameterValues values) {
            context.capturedValues = values;
        }
    }

    public static class DefaultActionCommand extends MyActionCommand {
        @Override
        public void run() {
            context.wasDefaultCommandExecuted = true;
        }
    }

    public static class MyRoutingContext {
        public boolean wasMyActionCommandExecuted = false;
        public boolean wasDefaultCommandExecuted = false;
        public CapturedParameterValues capturedValues;
    }

    private static class MapperObjectContainer {
        private UriPathSegmentActionMapper[] mappers = new UriPathSegmentActionMapper[1];

        public MapperObjectContainer() {
            this(1);
        }

        public void put(UriPathSegmentActionMapper mapper, int index) {
            mappers[index] = mapper;
        }

        public void put(UriPathSegmentActionMapper mapper) {
            put(mapper, 0);
        }

        public MapperObjectContainer(int numberOfMappers) {
            this.mappers = new UriPathSegmentActionMapper[numberOfMappers];
        }

        public UriPathSegmentActionMapper get(int index) {
            return mappers[index];
        }
    }

}