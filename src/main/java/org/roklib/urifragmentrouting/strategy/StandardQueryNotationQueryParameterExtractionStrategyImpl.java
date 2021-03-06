package org.roklib.urifragmentrouting.strategy;

import org.roklib.urifragmentrouting.helper.UriEncoderDecoder;

import java.util.*;
import java.util.regex.Pattern;

import static org.roklib.urifragmentrouting.helper.UriEncoderDecoder.decodeUriFragment;

/**
 * Default implementation class for interface {@link QueryParameterExtractionStrategy} which uses the standard URL query
 * String notation for adding URI parameters to a URI fragment. The following example shows a URI fragment with two URI
 * parameters in query mode:
 * <p>
 * <tt>/view/products?id=42&amp;expand=details</tt>
 * <p>
 * If any of the parameter names or values contains one of the special separator characters (?, =, and &amp;) these will
 * be properly encoded and decoded, so that the parameter extraction process will not be confused.
 */
public class StandardQueryNotationQueryParameterExtractionStrategyImpl implements QueryParameterExtractionStrategy {

    @Override
    public Map<String, String> extractQueryParameters(final String uriFragment) {
        if (uriFragment == null || hasNoParameters(uriFragment)) {
            return Collections.emptyMap();
        }

        final String parameters = uriFragment.substring(uriFragment.indexOf('?') + 1);
        if ("".equals(parameters.trim())) {
            return Collections.emptyMap();
        }

        final Map<String, String> resultMap = new HashMap<>();
        Arrays.stream(parameters.split("&")).forEach(parameter -> {
            if (!parameter.contains("=")) {
                resultMap.put(decodeUriFragment(parameter), "");
            } else {
                final String parameterName = parameter.substring(0, parameter.indexOf('='));
                final String parameterValue = parameter.substring(parameter.indexOf('=') + 1, parameter.length());
                resultMap.put(decodeUriFragment(parameterName), decodeSpecialChars(decodeUriFragment(parameterValue)));
            }
        });
        return resultMap;
    }

    @Override
    public String stripQueryParametersFromUriFragment(final String uriFragment) {
        if (uriFragment == null) {
            return null;
        }

        if (hasNoParameters(uriFragment)) {
            return uriFragment;
        }

        return uriFragment.substring(0, uriFragment.indexOf('?'));
    }

    @Override
    public String assembleQueryParameterSectionForUriFragment(final Map<String, String> forParameters) {
        if (forParameters == null || forParameters.isEmpty()) {
            return "";
        }
        final StringJoiner joiner = new StringJoiner("&");

        forParameters.entrySet().forEach(entry -> joiner.add(entry.getKey() + "=" + encodeSpecialChars(entry.getValue())));

        final String parameterList = UriEncoderDecoder.encodeUriFragment(joiner.toString());
        return parameterList.length() > 0 ? "?" + parameterList : "";
    }

    private final static Pattern encodedEqualsPattern = Pattern.compile("%3[Dd]");
    private final static Pattern encodedAmpersandPattern = Pattern.compile("%26");
    private final static Pattern encodedPercentPattern = Pattern.compile("%25");
    private final static Pattern equalsPattern = Pattern.compile("=");
    private final static Pattern ampersandPattern = Pattern.compile("&");
    private final static Pattern percentPattern = Pattern.compile("%");

    private String decodeSpecialChars(final String value) {
        String result = encodedEqualsPattern.matcher(value).replaceAll("=");
        result = encodedAmpersandPattern.matcher(result).replaceAll("&");
        return encodedPercentPattern.matcher(result).replaceAll("%");
    }

    private String encodeSpecialChars(final String value) {
        String result = percentPattern.matcher(value).replaceAll("%25");
        result = equalsPattern.matcher(result).replaceAll("%3D");
        return ampersandPattern.matcher(result).replaceAll("%26");
    }

    private boolean hasNoParameters(final String uriFragment) {
        return !uriFragment.contains("?");
    }
}
