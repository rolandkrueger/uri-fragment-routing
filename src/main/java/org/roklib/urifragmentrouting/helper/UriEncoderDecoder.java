package org.roklib.urifragmentrouting.helper;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Helper class for encoding/decoding URI fragments according to RFC 2396. This class is used to encode user-defined
 * data, such as URI parameter values or URI fragment path segments, before it is added to an assembled URI fragment. It
 * is used to decode this data when a URI fragment is interpreted. By that, reserved characters such as '#' or '%' can
 * be used in this type of data without breaking the URI fragment interpretation process.
 */
public final class UriEncoderDecoder {

    private UriEncoderDecoder() {
    }

    /**
     * Encodes the given String according to the encoding rules for URI fragments as specified in RFC 2396. For example,
     * the String <tt>'#/%'</tt> will be encoded into <tt>'%23/%25'</tt>.
     *
     * @param term the String to be encoded
     *
     * @return the encoded String
     */
    public static String encodeUriFragment(final String term) {
        try {
            return new URI("http", "none", term).getRawFragment();
        } catch (final URISyntaxException e) {
            throw new AssertionError("Exception should not happen.");
        }
    }

    /**
     * Decodes the given input String by reverting the encoding done by {@link #encodeUriFragment(String)}.
     *
     * @param input the encoded input String
     *
     * @return the decoded String
     */
    public static String decodeUriFragment(final String input) {
        try {
            return new URI("http://none#" + input).getFragment();
        } catch (final URISyntaxException e) {
            throw new AssertionError("Should not happen.");
        }
    }
}
