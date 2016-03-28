package org.roklib.urifragmentrouting.parameter;

/**
 * Enumeration to specify the strategy with which URI fragment parameters are to be extracted from a URI fragment.
 *
 * @author Roland Krüger
 */
public enum ParameterMode {
    /**
     * Specifies that the URI fragment parameters are formatted like URL query parameters.
     */
    QUERY,
    /**
     *
     */
    DIRECTORY,
    /**
     *
     */
    DIRECTORY_WITH_NAMES
}
