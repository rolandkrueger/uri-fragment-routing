package org.roklib.webapps.uridispatching.strategy;

import org.roklib.webapps.uridispatching.helper.UriEncoderDecoder;

import java.util.*;

import static org.roklib.webapps.uridispatching.helper.UriEncoderDecoder.decodeUriFragment;

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
                        .computeIfAbsent(decodeUriFragment(parameter), k -> new LinkedList<>())
                        .add("");
            } else {
                String parameterName = parameter.substring(0, parameter.indexOf('='));
                String parameterValue = parameter.substring(parameter.indexOf('=') + 1, parameter.length());
                resultMap
                        .computeIfAbsent(decodeUriFragment(parameterName), k -> new LinkedList<>())
                        .add(decodeUriFragment(parameterValue));
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

    @Override
    public String assembleQueryParameterSectionForUriFragment(Map<String, List<String>> forParameters) {
        if (forParameters == null || forParameters.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&");

        forParameters.entrySet().forEach(entry -> {
            if (entry.getValue() != null) {
                entry.getValue().forEach(value -> {
                    joiner.add(entry.getKey() + "=" + value);
                });
            }
        });

        final String parameterList = UriEncoderDecoder.encodeUriFragment(joiner.toString());
        return parameterList.length() > 0 ? "?" + parameterList : "";
    }

    private boolean hasParameters(String uriFragment) {
        return uriFragment.contains("?");
    }
}
