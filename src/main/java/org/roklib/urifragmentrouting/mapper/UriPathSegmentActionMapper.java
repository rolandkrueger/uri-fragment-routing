package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Every path segment action mapper is responsible for one path element of a URI fragment.
 */
public interface UriPathSegmentActionMapper extends Serializable {
    Class<? extends UriActionCommand> interpretTokens(CapturedParameterValues capturedParameterValues,
                                                      String currentMapperName, List<String> uriTokens,
                                                      Map<String, String> queryParameters,
                                                      ParameterMode parameterMode);

    String getMapperName();

    void setActionCommandClass(Class<? extends UriActionCommand> command);

    Class<? extends UriActionCommand> getActionCommand();

    void registerURIParameter(UriParameter<?> parameter);

    UriPathSegmentActionMapper getParentMapper();

    void setParentMapper(AbstractUriPathSegmentActionMapper parent);

    void assembleUriFragmentTokens(CapturedParameterValues capturedParameterValues, List<String> tokens, ParameterMode parameterMode);

    boolean isResponsibleForToken(String uriToken);

    void getMapperOverview(String path, List<String> mapperOverviewList);
}
