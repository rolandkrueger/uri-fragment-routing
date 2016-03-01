package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValuesImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface UriPathSegmentActionMapper extends Serializable {
    enum ParameterMode {
        QUERY, DIRECTORY, DIRECTORY_WITH_NAMES
    }

    Class<? extends UriActionCommand> interpretTokens(CapturedParameterValuesImpl capturedParameterValues,
                                                      String currentMapperName, List<String> uriTokens,
                                                      Map<String, String> queryParameters,
                                                      ParameterMode parameterMode);

    void registerURIParameter(UriParameter<?> parameter);

    void assembleUriFragmentTokens(CapturedParameterValues capturedParameterValues, List<String> tokens, ParameterMode parameterMode);
}
