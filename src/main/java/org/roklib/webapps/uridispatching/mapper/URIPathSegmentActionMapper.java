package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.ConsumedParameterValues;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface URIPathSegmentActionMapper extends Serializable {
    enum ParameterMode {
        QUERY, DIRECTORY, DIRECTORY_WITH_NAMES
    }

    Class<? extends URIActionCommand> interpretTokens(ConsumedParameterValues consumedParameterValues,
                                                      List<String> uriTokens,
                                                      Map<String, List<String>> parameters,
                                                      ParameterMode parameterMode);
}
