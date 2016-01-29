package org.roklib.webapps.uridispatching.strategy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author Roland Krüger
 */
public class StandardQueryNotationQueryParameterExtractionStrategyImpl implements QueryParameterExtractionStrategy {

    @Override
    public Map<String, List<String>> extractQueryParameters(String uriFragment) {
        if (uriFragment == null || ! hasParameters(uriFragment)) {
            return Collections.emptyMap();
        }

        String parameters = uriFragment.substring(uriFragment.indexOf('?') + 1);
        if ("".equals(parameters.trim())) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> resultMap = new HashMap<>();
        Arrays.stream(parameters.split("&")).forEach(parameter -> {
            if (! parameter.contains("=")) {
                resultMap
                        .computeIfAbsent(urlDecode(parameter), k -> new LinkedList<>())
                        .add("");
            } else {
                String parameterName = parameter.substring(0, parameter.indexOf('='));
                String parameterValue = parameter.substring(parameter.indexOf('=') + 1, parameter.length());
                resultMap
                        .computeIfAbsent(urlDecode(parameterName), k -> new LinkedList<>())
                        .add(urlDecode(parameterValue));
            }
        });
        return resultMap;
    }

    @Override
    public String stripQueryParametersFromUriFragment(String uriFragment) {
        if (uriFragment == null) {
            return null;
        }

        if (! hasParameters(uriFragment)) {
            return uriFragment;
        }

        return uriFragment.substring(0, uriFragment.indexOf('?'));
    }

    private boolean hasParameters(String uriFragment) {
        return uriFragment.contains("?");
    }

    private String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new AssertionError("UTF-8 encoding not supported on this platform", unsupportedEncodingException);
        }
    }
}
