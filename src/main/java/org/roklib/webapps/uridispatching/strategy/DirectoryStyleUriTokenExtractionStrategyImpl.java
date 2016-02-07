package org.roklib.webapps.uridispatching.strategy;

import org.roklib.webapps.uridispatching.helper.UriEncoderDecoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.roklib.webapps.uridispatching.helper.UriEncoderDecoder.encodeUriFragment;

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

        return Arrays.stream(uriFragment.split("/")).map(UriEncoderDecoder::decodeUriFragment).collect(Collectors.toList());
    }

    @Override
    public String assembleUriFragmentFromTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("/");
        tokens.forEach(joiner::add);
        return encodeUriFragment(joiner.toString());
    }


}
