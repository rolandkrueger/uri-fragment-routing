package org.roklib.urifragmentrouting.strategy;

import java.util.Map;

/**
 * Strategy for extracting parameters from a URI fragment which are contained in the fragment in query format.
 * Parameters which are arranged in this format are clustered in the end of the URI fragment and thus are not juxtaposed
 * with the path segments to which they belong. Consider, for example, the following URI fragment which contains URI
 * parameters in query mode:
 * <p>
 * <tt>/view/products?id=42&expand=details</tt>
 * <p>
 * Here, the standard syntax for URL query parameters is used to separate the URI parameters from the URI fragment path.
 * In this example the part <tt>id=42&expand=details</tt> is the query parameter section of the URI fragment, and
 * <tt>'?'</tt> is the character that separates those two parts from each other. As can be seen in this example, all URI
 * parameters are clustered at the end of the URI fragment. In directory mode using directory names (see {@link
 * org.roklib.urifragmentrouting.parameter.ParameterMode}), the URI fragment would look like this:
 * <tt>/view/expand/details/products/id/42</tt>.
 * <p>
 * Implementation classes for this strategy have to take care that parameter names and parameter values are properly
 * encoded and decoded, so that special characters used to separate the parameters from each other may be contained in
 * the user data without confusing the parameter extraction process. In the example above, this would be ?, =, and &.
 *
 * @see StandardQueryNotationQueryParameterExtractionStrategyImpl
 */
public interface QueryParameterExtractionStrategy {

    /**
     * Extracts all URI parameters contained in the given URI fragment in query mode and pass them back as a parameter
     * map. For example, given the following URI fragment:
     * <p>
     * <tt>/view/products?id=42&expand=details</tt>
     * <p>
     * this method would return the below map:
     * <p>
     * <pre>
     *     {
     *       "id"     => "42",
     *       "expand" => "details"
     *     }
     * </pre>
     *
     * @param uriFragment the URI fragment from which URI parameters in query mode are to be extracted
     * @return a map containing the extracted parameter values where the keys represent parameter names and the values
     * represent parameter values. May return an empty map, but must not return {@code null}.
     */
    Map<String, String> extractQueryParameters(String uriFragment);

    /**
     * Removes the section from the given URI fragment which contains the query parameters. For example, if called for
     * the following URI fragment: <tt>/view/products?id=42&expand=details</tt>, the method will return the String
     * <tt>/view/products</tt>.
     *
     * @param uriFragment the URI fragment from which the query parameter section is to be stripped
     * @return the given URI fragment without the section that contains query parameters and without any separator
     * character (such as '?')
     */
    String stripQueryParametersFromUriFragment(String uriFragment);

    /**
     * Inverse operation to {@link #extractQueryParameters(String)}: receives a map of URI parameter values and returns
     * the query String for these parameters to be appended to the URI fragment. For example, the following map:
     * <p>
     * <pre>
     *     {
     *       "id"     => "42",
     *       "expand" => "details"
     *     }
     * </pre>
     * <p>
     * will be transformed into the below query String:
     * <p>
     * <tt>?id=42&expand=details</tt>
     * <p>
     * Note that the separator char which separates the query String from the rest of the URI fragment has to be
     * included in the query String as well.
     *
     * @param forParameters map of parameters to be assembled into a query String
     * @return the query String for the given parameter values
     */
    String assembleQueryParameterSectionForUriFragment(Map<String, String> forParameters);
}
