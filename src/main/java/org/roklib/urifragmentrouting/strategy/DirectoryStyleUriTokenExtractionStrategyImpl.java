package org.roklib.urifragmentrouting.strategy;

import org.roklib.urifragmentrouting.helper.UriEncoderDecoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.roklib.urifragmentrouting.helper.UriEncoderDecoder.encodeUriFragment;

/**
 * Default implementation of the {@link UriTokenExtractionStrategy} which splits a URI fragment along the path separator
 * character <tt>'/'</tt>. This implementation takes care that the two special characters <tt>'/'</tt> and <tt>%</tt>
 * contained in the list of URI tokens to be assembled into a URI fragment with {@link
 * #assembleUriFragmentFromTokens(List)} are properly encoded before they are added to the URI fragment. By that, user
 * data that contains the separator character will not confuse the token extraction process.
 */
public class DirectoryStyleUriTokenExtractionStrategyImpl implements UriTokenExtractionStrategy {
    @Override
    public List<String> extractUriTokens(String uriFragment) {
        if (uriFragment == null || "".equals(uriFragment.trim())) {
            return Collections.emptyList();
        }

        return Arrays.stream(uriFragment.split("/"))
                .map(s -> UriEncoderDecoder.decodeUriFragment(decodeSpecialChars(s)))
                .collect(Collectors.toList());
    }

    @Override
    public String assembleUriFragmentFromTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("/");
        tokens.forEach(s -> joiner.add(encodeSpecialChars(s)));
        return encodeUriFragment(joiner.toString());
    }

    private final static Pattern encodedSlashPattern = Pattern.compile("%2[Ff]");
    private final static Pattern encodedPercentPattern = Pattern.compile("%25");
    private final static Pattern slashPattern = Pattern.compile("/");
    private final static Pattern percentPattern = Pattern.compile("%");

    private String decodeSpecialChars(String value) {
        final String result = encodedSlashPattern.matcher(value).replaceAll("/");
        return encodedPercentPattern.matcher(result).replaceAll("%");
    }

    private String encodeSpecialChars(String value) {
        String result = percentPattern.matcher(value).replaceAll("%25");
        return slashPattern.matcher(result).replaceAll("%2F");
    }
}
