package org.roklib.urifragmentrouting.strategy;

import java.util.List;

/**
 * Strategy interface that defines how a URI fragment is to be disassembled into a list of String tokens. A URI fragment
 * which is to be interpreted by the {@link org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree}
 * consists of a number of path segments managed by objects of type {@link org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper
 * UriPathSegmentActionMapper} and URI parameters managed by objects of type {@link
 * org.roklib.urifragmentrouting.parameter.UriParameter UriParameter}. In order to interpret such a URI fragment, it has
 * to be split into a list of String tokens which can then be processed from beginning to end.
 * <p>
 * Implementations of this interface define this process in both directions, i. e. how a URI fragment is turned into a
 * list of Strings and how a list of Strings is assembled into a String. Disassembling a URI fragment into a list of
 * Strings and reassembling this list into a String must yield the original URI fragment.
 * <p>
 * The strategy for extracting URI fragment tokens has to define the manner in which the tokens are separated from each
 * other. For example, the default implementation of this interface {@link DirectoryStyleUriTokenExtractionStrategyImpl}
 * separates the URI tokens with the path separator character <tt>'/'</tt>. That is, the URI fragment
 * <p>
 * <tt>/uri/fragment/path/id/17</tt>
 * <p>
 * will be split into the following token list:
 * <p>
 * <tt>{"uri", "fragment", "path", "id", "17"}</tt>
 * <p>
 * Implementation classes for this strategy have to take care that any character which is used as a separator in a URI
 * fragment is encoded and decoded properly in the URI tokens used to assemble a URI fragment.
 *
 * @see DirectoryStyleUriTokenExtractionStrategyImpl
 */
public interface UriTokenExtractionStrategy {
    /**
     * Converts a URI fragment String into a list of Strings (URI tokens) which can be interpreted by the {@link
     * org.roklib.urifragmentrouting.UriActionMapperTree}.
     *
     * @param uriFragment the URI fragment from which a list of URI tokens is to be extracted
     *
     * @return a list of Strings that contains the extracted URI tokens for the given URI fragment. This list can be
     * empty but must not be <code>null</code>.
     */
    List<String> extractUriTokens(String uriFragment);

    /**
     * Assembles a list of URI fragment tokens into a URI fragment. This operation reverts the token extraction done by
     * {@link #extractUriTokens(String)}. When the String returned by this method is split back into a token list with
     * {@link #extractUriTokens(String)} the resulting list must be the same as was originally fed into this method.
     * <p>
     * For example, when the list
     * <p>
     * <tt>{"uri", "fragment", "path", "id", "17"}</tt>
     * <p>
     * is assembled into the String <tt>/uri/fragment/path/id/17</tt> then feeding this resulting String into {@link
     * #extractUriTokens(String)} has to result into the same list.
     *
     * @param tokens list of URI fragment tokens to be assembled into a URI fragment
     *
     * @return a URI fragment assembled from the tokens from the given String list
     */
    String assembleUriFragmentFromTokens(List<String> tokens);
}
