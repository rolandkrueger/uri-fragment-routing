package org.roklib.webapps.uridispatching;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValues;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Roland Krüger
 */
public class AssembleUriFragmentForMapperTest {

    private Map<String, AbstractUriPathSegmentActionMapper> mappers;
    private CapturedParameterValues values;

    @Before
    public void setUp() {
        mappers = new HashMap<>();
        // @formatter:off
        UriActionMapperTree.create().buildMapperTree()
                .map("login").onAction(SomeActionClass.class).finishMapper(mapper -> storeMapper("login", mapper))
                .mapSubtree("admin")
                    .onSubtree()
                    .map("users").onAction(SomeActionClass.class).finishMapper(mapper -> storeMapper("users", mapper))
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

    @Test
    public void path_for_single_mapper() {
        AbstractUriPathSegmentActionMapper mapper = mappers.get("login");
        String fragment = mapper.assembleUriFragment(values);
        assertThat(fragment, is(equalTo("login")));
    }

    @Test
    public void path_for_subtree_mapper_with_parameters() {
        AbstractUriPathSegmentActionMapper mapper = mappers.get("customer");
        String fragment = mapper.assembleUriFragment(values);
        assertThat(fragment, is(equalTo("profiles/type/long/customer/id/17/lang/de")));
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