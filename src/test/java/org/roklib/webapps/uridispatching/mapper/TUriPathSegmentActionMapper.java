package org.roklib.webapps.uridispatching.mapper;


import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.parameter.UriParameter;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

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
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, List<String>> parameters,
                                                                    ParameterMode parameterMode) {
        return command;
    }

    public void registerURLParameterForTest(UriParameter<?> parameter) {
        registerURIParameter(parameter);
    }
}
