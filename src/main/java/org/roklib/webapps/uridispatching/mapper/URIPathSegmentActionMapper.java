package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.AbstractURIActionCommand;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface URIPathSegmentActionMapper extends Serializable {
    enum ParameterMode {
        QUERY, DIRECTORY, DIRECTORY_WITH_NAMES
    }

    AbstractURIActionCommand handleURI(List<String> uriTokens, Map<String, List<String>> parameters,
                                       ParameterMode parameterMode);
}
