package org.roklib.urifragmentrouting.parameter;

/**
 * Enumeration to specify the strategy with which URI fragment parameters are to be extracted from a given URI
 * fragment.
 */
public enum ParameterMode {
    /**
     * Specifies that the URI fragment parameters are formatted like URL query parameters.
     * <p>
     * Example: If there are to path segment mappers defined (e. g. <tt>category</tt> and <tt>product</tt>) each of
     * which has one registered parameter (e. g. <tt>category</tt> is assigned a parameter <tt>name</tt> and
     * <tt>product</tt> is assigned a parameter <tt>id</tt>) then the following URI fragment would be an example for
     * query mode:
     * <p>
     * {@code /category/product?name=tools&id=42}
     * <p>
     * Note that if this parameter mode is used a parameter can only be used for one path segment mapper. The same
     * parameter id cannot be used with more than one mapper. This is because in query mode there is no way for a
     * parameter value to indicate to which mapper it belongs.
     */
    QUERY,
    /**
     * Specifies that URI parameters registered for a path segment mapper are listed after the corresponding mapper name
     * without including the parameter ids in the URI fragment.
     * <p>
     * Example: If there are to path segment mappers defined (e. g. <tt>category</tt> and <tt>product</tt>) each of
     * which has one registered parameter (e. g. <tt>category</tt> is assigned a parameter <tt>name</tt> and
     * <tt>product</tt> is assigned a parameter <tt>id</tt>) then the following URI fragment would be an example for
     * directory mode:
     * <p>
     * <code>/category/tools/product/42</code>
     */
    DIRECTORY,
    /**
     * Specifies that URI parameters registered for a path segment mapper are listed after the corresponding mapper name
     * including the parameter ids in the URI fragment.
     * <p>
     * Example: If there are to path segment mappers defined (e. g. <tt>category</tt> and <tt>product</tt>) each of
     * which has one registered parameter (e. g. <tt>category</tt> is assigned a parameter <tt>name</tt> and
     * <tt>product</tt> is assigned a parameter <tt>id</tt>) then the following URI fragment would be an example for
     * directory with names mode:
     * <p>
     * <code>/category/name/tools/product/id/42</code>
     */
    DIRECTORY_WITH_NAMES
}
