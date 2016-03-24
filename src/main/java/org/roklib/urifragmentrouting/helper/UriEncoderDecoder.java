package org.roklib.urifragmentrouting.helper;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Helper class for encoding/decoding URI fragments according to RFC 2396.
 *
 * @author Roland Krüger
 */
public final class UriEncoderDecoder {

    private UriEncoderDecoder() {
    }

    public static String decodeUriFragment(String input) {
        try {
            return new URI("http://none#" + input).getFragment();
        } catch (URISyntaxException e) {
            throw new AssertionError("Should not happen.");
        }
    }

    public static String encodeUriFragment(String term) {
        try {
            return new URI("http", "none", term).getRawFragment();
        } catch (URISyntaxException e) {
            throw new AssertionError("Exception should not happen.");
        }
    }
}
