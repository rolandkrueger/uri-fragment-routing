package org.roklib.urifragmentrouting.strategy;

import java.util.List;

public interface UriTokenExtractionStrategy {
    List<String> extractUriTokens(String uriFragment);

    String assembleUriFragmentFromTokens(List<String> tokens);
}
