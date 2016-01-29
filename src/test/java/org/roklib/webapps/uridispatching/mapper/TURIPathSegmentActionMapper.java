package org.roklib.webapps.uridispatching.mapper;


import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.parameter.URIParameter;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValues;

import java.util.List;
import java.util.Map;

public class TURIPathSegmentActionMapper extends DispatchingURIPathSegmentActionMapper {
    private static final long serialVersionUID = 6202866717473440168L;

    private Class<? extends URIActionCommand> command;

    public TURIPathSegmentActionMapper(String actionName, Class<? extends URIActionCommand> command) {
        super(actionName);
        this.command = command;
    }

    @Override
    protected Class<? extends URIActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                    List<String> uriTokens,
                                                                    Map<String, List<String>> parameters,
                                                                    ParameterMode parameterMode) {
        return command;
    }

    public void registerURLParameterForTest(URIParameter<?> parameter) {
        registerURIParameter(parameter);
    }
}
