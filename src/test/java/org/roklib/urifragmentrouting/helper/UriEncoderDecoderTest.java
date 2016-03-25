package org.roklib.urifragmentrouting.helper;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UriEncoderDecoderTest {

    @Test
    public void testEncodeDecodeUriFragment() throws Exception {
        final String encodedString = UriEncoderDecoder.encodeUriFragment("#/%");
        assertThat(encodedString, is("%23/%25"));
        
        final String decodedString = UriEncoderDecoder.decodeUriFragment(encodedString);
        assertThat(decodedString, is("#/%"));
    }
}