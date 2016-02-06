package org.roklib.webapps.uridispatching.strategy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
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

        return Arrays.stream(uriFragment.split("/")).map(this::decodeUriFragment).collect(Collectors.toList());
    }

    private String decodeUriFragment(String input) {
        try {
            return new URI("http://none#" + input).getFragment();
        } catch (URISyntaxException e) {
            throw new AssertionError("Should not happen.");
        }
    }
}
