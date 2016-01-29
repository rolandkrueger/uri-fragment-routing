package org.roklib.webapps.uridispatching.strategy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation classes for this strategy have to take care that the individual tokens are properly URL
 * decoded.
 *
 * @author Roland Krüger
 */
public class DirectoryStyleUriTokenExtractionStrategyImpl implements UriTokenExtractionStrategy {
    @Override
    public List<String> extractUriTokens(String uriFragment) {
        if (uriFragment == null || "".equals(uriFragment.trim())) {
            return Collections.emptyList();
        }

        return Arrays.stream(uriFragment.split("/")).map(this::urlDecode).collect(Collectors.toList());
    }

    private String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new AssertionError("UTF-8 encoding not supported on this platform", unsupportedEncodingException);
        }
    }
}
