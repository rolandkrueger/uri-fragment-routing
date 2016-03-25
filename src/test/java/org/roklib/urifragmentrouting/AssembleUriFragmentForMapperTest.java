package org.roklib.urifragmentrouting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.SimpleUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.Point2DUriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValuesImpl;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class AssembleUriFragmentForMapperTest {

    private Map<String, AbstractUriPathSegmentActionMapper> mappers;
    private CapturedParameterValues values;
    private UriActionMapperTree mapperTree;

    @Before
    public void setUp() {
        mappers = new HashMap<>();
        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .map("login").onAction(SomeActionClass.class).finishMapper(mapper -> storeMapper("login", mapper))
                .map("location").onAction(SomeActionClass.class)
                    .withParameter(new Point2DUriParameter("coord", "x", "y")).finishMapper(mapper -> storeMapper("location", mapper))
                .mapSubtree("admin")
                    .onSubtree()
                    .map("users").onAction(SomeActionClass.class).finishMapper(mapper -> storeMapper("users", mapper))
                .finishMapper()
                .mapSubtree("profiles").withSingleValuedParameter("type").forType(String.class).noDefault()
                    .onSubtree()
                    .map("customer").onAction(SomeActionClass.class)
                        .withSingleValuedParameter("id").forType(Integer.class).noDefault()
                        .withSingleValuedParameter("lang").forType(String.class).noDefault()
                        .finishMapper(mapper -> storeMapper("customer", mapper))
                .build();
        // @formatter:on
        values = new CapturedParameterValuesImpl();
    }

    @After
    public void printMapperTree() {
        if (mapperTree != null) {
            System.out.println("Testing with following mapper tree:\n");
            mapperTree.print(System.out);
            System.out.println("----------------------------------------");
        }
    }

    @Test
    public void assemble_fragment_for_single_mapper() {
        AbstractUriPathSegmentActionMapper mapper = mappers.get("login");
        String fragment = mapperTree.assembleUriFragment(values, mapper);
        assertThat(fragment, is(equalTo("login")));
    }

    @Test
    public void assemble_fragment_for_single_mapper_with_parameter() {
        AbstractUriPathSegmentActionMapper mapper = mappers.get("location");
        values.setValueFor("location", "coord", ParameterValue.forValue(new Point2D.Double(10.0d, 20.0d)));

        String fragment = mapperTree.assembleUriFragment(values, mapper);
        assertThat(fragment, is(equalTo("location/x/10.0/y/20.0")));
    }

    @Test
    public void assemble_fragment_for_subtree_mapper_with_parameters() {
        AbstractUriPathSegmentActionMapper mapper = mappers.get("customer");
        values.setValueFor("profiles", "type", ParameterValue.forValue("long"));
        values.setValueFor("customer", "id", ParameterValue.forValue(17));
        values.setValueFor("customer", "lang", ParameterValue.forValue("de"));
        String fragment = mapperTree.assembleUriFragment(values, mapper);
        assertThat(fragment, is(equalTo("profiles/type/long/customer/id/17/lang/de")));
    }

    @Test
    public void assemble_fragment_for_parameters_with_mode_directory_without_names() {
        mapperTree = getMapperTreeForParameterMode(ParameterMode.DIRECTORY);
        values.setValueFor("customer", "name", ParameterValue.forValue("ACME Corp."));
        values.setValueFor("show", "id", ParameterValue.forValue(17));
        values.setValueFor("show", "lang", ParameterValue.forValue("de"));
        String fragment = mapperTree.assembleUriFragment(values, mappers.get("show"));
        assertThat(fragment, is(equalTo("customer/ACME%20Corp./show/17/de")));
    }

    @Test
    public void assemble_fragment_for_parameters_with_query_mode() {
        mapperTree = getMapperTreeForParameterMode(ParameterMode.QUERY);
        values.setValueFor("customer", "name", ParameterValue.forValue("ACME Corp."));
        values.setValueFor("show", "id", ParameterValue.forValue(17));
        values.setValueFor("show", "lang", ParameterValue.forValue("de"));
        String fragment = mapperTree.assembleUriFragment(values, mappers.get("show"));
        assertThat(fragment, startsWith("customer/show?"));
        assertThat(fragment + " doesn't match expected regex", fragment.matches("customer/show\\?((name=ACME%20Corp.|id=17|lang=de)&?){3}"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void using_mapper_not_in_tree_is_not_allowed() {
        mapperTree.assembleUriFragment(values, new SimpleUriPathSegmentActionMapper("unknown"));
    }

    @Test(expected = NullPointerException.class)
    public void use_null_mapper_throws_exception() {
        mapperTree.assembleUriFragment(values, null);
    }

    private UriActionMapperTree getMapperTreeForParameterMode(ParameterMode mode) {
        // @formatter:off
        return UriActionMapperTree.create().useParameterMode(mode).buildMapperTree()
                .mapSubtree("customer")
                    .withSingleValuedParameter("name").forType(String.class).noDefault()
                    .onSubtree()
                        .map("show").onAction(SomeActionClass.class)
                            .withSingleValuedParameter("id").forType(Integer.class).noDefault()
                            .withSingleValuedParameter("lang").forType(String.class).noDefault()
                            .finishMapper(mapper -> storeMapper("show", mapper))
                    .finishMapper()
                .build();
        // @formatter:on
    }

    private void storeMapper(String id, AbstractUriPathSegmentActionMapper mapper) {
        mappers.put(id, mapper);
    }

    public static class SomeActionClass implements UriActionCommand {
        @Override
        public void run() {
        }
    }

}