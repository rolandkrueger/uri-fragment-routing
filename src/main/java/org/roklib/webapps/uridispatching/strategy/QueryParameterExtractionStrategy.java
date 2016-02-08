package org.roklib.webapps.uridispatching.strategy;

import java.util.Map;

/**
 * Implementation classes for this strategy have to take care that parameter names and parameter values are properly URL
 * decoded.
 *
 * @author Roland Krüger
 */
public interface QueryParameterExtractionStrategy {
    Map<String, String> extractQueryParameters(String uriFragment);

    String stripQueryParametersFromUriFragment(String uriFragment);

    String assembleQueryParameterSectionForUriFragment(Map<String, String> forParameters);
}
