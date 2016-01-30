package org.roklib.webapps.uridispatching.strategy;

import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * @author Roland Krüger
 */
public class DirectoryStyleUriTokenExtractionStrategyImplTest {

    private DirectoryStyleUriTokenExtractionStrategyImpl strategy;

    @Before
    public void setUp() throws Exception {
        strategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
    }

    @Test
    public void testExtractUriTokens() throws Exception {
        List<String> result = strategy.extractUriTokens("/path/to/mapper/");
        assertThat(result, is(contains("", "path", "to", "mapper")));
    }

    @Test
    public void testExtractUriTokens_empty_string() throws Exception {
        List<String> result = strategy.extractUriTokens("");
        assertThat(result, is(emptyCollectionOf(String.class)));
    }

    @Test
    public void testExtractUriTokens_null_string() throws Exception {
        List<String> result = strategy.extractUriTokens(null);
        assertThat(result, is(emptyCollectionOf(String.class)));
    }

    @Test
    public void testExtractUriTokens_url_decodes_tokens() throws Exception {
        List<String> result = strategy.extractUriTokens("/path/to/mapper/" + URLEncoder.encode("with/slash", "UTF-8"));
        assertThat(result, is(contains("", "path", "to", "mapper", "with/slash")));
    }

    @Test
    public void extracted_token_list_is_mutable() {
        List<String> result = strategy.extractUriTokens("/path/to/mapper/");
        result.clear();
    }
}