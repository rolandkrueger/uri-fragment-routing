package org.roklib.urifragmentrouting.mapper;


import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.List;
import java.util.Map;

public class TUriPathSegmentActionMapper extends DispatchingUriPathSegmentActionMapper {
    private static final long serialVersionUID = 6202866717473440168L;

    private Class<? extends UriActionCommand> command;

    public TUriPathSegmentActionMapper(String actionName, Class<? extends UriActionCommand> command) {
        super(actionName);
        this.command = command;
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> parameters,
                                                                    ParameterMode parameterMode) {
        return command;
    }

    public void registerURLParameterForTest(UriParameter<?> parameter) {
        registerURIParameter(parameter);
    }
}
