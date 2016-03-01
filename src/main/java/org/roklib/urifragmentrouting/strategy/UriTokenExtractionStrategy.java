package org.roklib.urifragmentrouting.strategy;

import java.util.List;

/**
 * @author Roland Krüger
 */
public interface UriTokenExtractionStrategy {
    List<String> extractUriTokens(String uriFragment);

    String assembleUriFragmentFromTokens(List<String> tokens);
}
