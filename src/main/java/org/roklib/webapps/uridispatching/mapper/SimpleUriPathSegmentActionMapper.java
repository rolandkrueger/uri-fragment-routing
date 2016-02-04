package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.List;
import java.util.Map;

/**
 * A simple URI action mapper that directly returns a predefined action command when the URI interpretation process
 * encounters this mapper. By that, {@link SimpleUriPathSegmentActionMapper}s always represent the last token of an
 * interpreted URI as they cannot dispatch to any sub-mappers.
 *
 * @author Roland Krüger
 */
public class SimpleUriPathSegmentActionMapper extends AbstractUriPathSegmentActionMapper {
    private static final long serialVersionUID = 8203362201388037000L;

    /**
     * Create a new {@link SimpleUriPathSegmentActionMapper} with the specified action name and action command.
     *
     * @param segmentName
     *         the name of the URI path segment this mapper is responsible for
     */
    public SimpleUriPathSegmentActionMapper(String segmentName) {
        super(segmentName);
    }

    public SimpleUriPathSegmentActionMapper(String segmentName, Class<? extends UriActionCommand> actionCommandClass) {
        super(segmentName);
        setActionCommandClass(actionCommandClass);
    }

    /**
     * Directly returns the URI action command passed in through the constructor. All method arguments are ignored.
     */
    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, List<String>> parameters,
                                                                    ParameterMode parameterMode) {
        return getActionCommand();
    }
}
