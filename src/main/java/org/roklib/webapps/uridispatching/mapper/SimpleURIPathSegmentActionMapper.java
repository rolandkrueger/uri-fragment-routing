package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.URIActionCommand;

import java.util.List;
import java.util.Map;

/**
 * A simple URI action mapper that directly returns a predefined action command when the URI interpretation process
 * encounters this mapper. By that, {@link SimpleURIPathSegmentActionMapper}s always represent the last token of an
 * interpreted URI as they cannot dispatch to any sub-mappers.
 *
 * @author Roland Krüger
 */
public class SimpleURIPathSegmentActionMapper extends org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper {
    private static final long serialVersionUID = 8203362201388037000L;

    /**
     * Create a new {@link SimpleURIPathSegmentActionMapper} with the specified action name and action command.
     *
     * @param segmentName the name of the URI path segment this mapper is responsible for
     */
    public SimpleURIPathSegmentActionMapper(String segmentName) {
        super(segmentName);
    }

    /**
     * Directly returns the URI action command passed in through the constructor. All method arguments are ignored.
     */
    @Override
    protected URIActionCommand handleURIImpl(List<String> uriTokens, Map<String, List<String>> parameters,
                                             ParameterMode parameterMode) {
        return getActionCommand();
    }
}
