package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface URIPathSegmentActionMapper extends Serializable {
    enum ParameterMode {
        QUERY, DIRECTORY, DIRECTORY_WITH_NAMES
    }

    Class<? extends URIActionCommand> interpretTokens(CapturedParameterValuesImpl capturedParameterValues,
                                                      String currentMapperName, List<String> uriTokens,
                                                      Map<String, List<String>> queryParameters,
                                                      ParameterMode parameterMode);
}
