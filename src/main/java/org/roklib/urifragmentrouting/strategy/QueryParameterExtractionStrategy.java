package org.roklib.urifragmentrouting.strategy;

import java.util.Map;

/**
 * Strategy for extracting parameters from a URI fragment which are contained in the fragment in query format.
 * Parameters which are arranged in this format are clustered in one place in the URI fragment and thus are not
 * juxtaposed with the path segments to which they belong. Consider, for example, the following URI fragment which
 * contains URI parameters in query mode:
 * <p>
 * <tt>/view/products?id=42&expand=details</tt>
 * <p>
 * Here the standard syntax for URL query parameters is used to separate the URI parameters from the URI fragment path.
 * In this example the part <tt>id=42&expand=details</tt> is the query parameter section of the URI fragment, and
 * <tt>'?'</tt> is the character that separates those two parts from each other.
 * <p>
 * Implementation classes for this strategy have to take care that parameter names and parameter values are properly
 * encoded and decoded, so that special characters used to separate the parameters from each other may be contained in
 * the user data. In the example above, this would be ?, =, and &.
 */
public interface QueryParameterExtractionStrategy {
    Map<String, String> extractQueryParameters(String uriFragment);

    /**
     * Removes the section from the given URI fragment which contains the query parameters. For example, if called for
     * the following URI fragment: <tt>/view/products?id=42&expand=details</tt>, the method will return the String
     * <tt>/view/products</tt>.
     *
     * @param uriFragment the URI fragment from which the query parameter section is to be stripped
     * @return the given URI fragment without the section that contains query parameters
     */
    String stripQueryParametersFromUriFragment(String uriFragment);

    String assembleQueryParameterSectionForUriFragment(Map<String, String> forParameters);
}
