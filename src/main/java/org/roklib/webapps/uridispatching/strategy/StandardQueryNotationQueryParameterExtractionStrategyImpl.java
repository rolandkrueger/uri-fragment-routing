package org.roklib.webapps.uridispatching.strategy;

import org.roklib.webapps.uridispatching.helper.UriEncoderDecoder;

import java.util.*;

import static org.roklib.webapps.uridispatching.helper.UriEncoderDecoder.decodeUriFragment;

/**
 * @author Roland Krüger
 */
public class StandardQueryNotationQueryParameterExtractionStrategyImpl implements QueryParameterExtractionStrategy {

    @Override
    public Map<String, String> extractQueryParameters(String uriFragment) {
        if (uriFragment == null || !hasParameters(uriFragment)) {
            return Collections.emptyMap();
        }

        String parameters = uriFragment.substring(uriFragment.indexOf('?') + 1);
        if ("".equals(parameters.trim())) {
            return Collections.emptyMap();
        }

        Map<String, String> resultMap = new HashMap<>();
        Arrays.stream(parameters.split("&")).forEach(parameter -> {
            if (!parameter.contains("=")) {
                resultMap.put(decodeUriFragment(parameter), "");
            } else {
                String parameterName = parameter.substring(0, parameter.indexOf('='));
                String parameterValue = parameter.substring(parameter.indexOf('=') + 1, parameter.length());
                resultMap.put(decodeUriFragment(parameterName), decodeSpecialChars(decodeUriFragment(parameterValue)));
            }
        });
        return resultMap;
    }

    @Override
    public String stripQueryParametersFromUriFragment(String uriFragment) {
        if (uriFragment == null) {
            return null;
        }

        if (!hasParameters(uriFragment)) {
            return uriFragment;
        }

        return uriFragment.substring(0, uriFragment.indexOf('?'));
    }

    @Override
    public String assembleQueryParameterSectionForUriFragment(Map<String, String> forParameters) {
        if (forParameters == null || forParameters.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&");

        forParameters.entrySet().forEach(entry -> {
            joiner.add(entry.getKey() + "=" + encodeSpecialChars(entry.getValue()));
        });

        final String parameterList = UriEncoderDecoder.encodeUriFragment(joiner.toString());
        return parameterList.length() > 0 ? "?" + parameterList : "";
    }

    private String decodeSpecialChars(String value) {
        return value.replaceAll("%3D", "=").replaceAll("%26", "&").replaceAll("%25", "%");
    }

    private String encodeSpecialChars(String value) {
        return value.replaceAll("%", "%25").replaceAll("=", "%3D").replaceAll("&", "%26");
    }

    private boolean hasParameters(String uriFragment) {
        return uriFragment.contains("?");
    }
}
