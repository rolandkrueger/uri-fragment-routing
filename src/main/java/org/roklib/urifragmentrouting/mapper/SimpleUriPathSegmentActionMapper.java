package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.helper.ActionCommandConfigurer;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A simple URI action mapper that directly returns its URI action command when the URI fragment interpretation process
 * reaches this mapper. By that, {@link SimpleUriPathSegmentActionMapper}s  represent the leaves of a URI action mapper
 * tree, i. e. they do not dispatch to any sub-mappers.
 * <p>
 * For the the URI fragment <tt>/users/profile</tt>, for instance, the path segment <tt>users</tt> will be handled by a
 * {@link DispatchingUriPathSegmentActionMapper} which passes the responsibility for handling the remaining part of the
 * URI fragment to the mapper for <tt>profile</tt>. This last path segment <tt>profile</tt> is handled by a {@link
 * SimpleUriPathSegmentActionMapper}.
 *
 * @see DispatchingUriPathSegmentActionMapper
 */
public class SimpleUriPathSegmentActionMapper extends AbstractUriPathSegmentActionMapper {
    private static final long serialVersionUID = 8203362201388037000L;
    private static final Logger LOG = LoggerFactory.getLogger(SimpleUriPathSegmentActionMapper.class);

    /**
     * Create a new {@link SimpleUriPathSegmentActionMapper} identified by the given mapper name. Simple action mappers
     * created with this constructor are responsible for handling URI fragment path segments with the same name as the
     * action mapper.
     *
     * @param mapperName the name of this mapper which is at the same time used to map path segments of the same name
     */
    public SimpleUriPathSegmentActionMapper(String mapperName) {
        super(mapperName);
    }

    /**
     * Create a new {@link SimpleUriPathSegmentActionMapper} identified by the given mapper name which is responsible
     * for handling path segments of the specified name.
     * <p>
     * This constructor can be used if the same path segment name is handled by more than one action mapper in different
     * places of the URI action mapper tree. For example, if the path segment name <tt>view</tt> shall be used in more
     * than one place in the URI action mapper tree like in the following URI fragments
     * <pre>
     *     /users/profile/view
     *     /groups/view
     * </pre>
     * then one of the two action mappers responsible for the <tt>view</tt> part cannot have the same mapper name. This
     * is because mapper names have to be unique in a URI action mapper tree. In this case, one of the two action
     * mappers for <tt>view</tt> has to get a mapper name that is different from <tt>view</tt>. The path segment name,
     * however, is <tt>view</tt> for both action mappers.
     *
     * @param mapperName     name of this mapper
     * @param pathSegment    the name of the path segment this action mapper is responsible for
     * @param commandFactory the action command factory for this action mapper
     */
    public SimpleUriPathSegmentActionMapper(String mapperName, String pathSegment, UriActionCommandFactory commandFactory) {
        super(mapperName, pathSegment);
        setActionCommandFactory(commandFactory);
    }

    /**
     * Directly returns the URI action command factory passed in through the constructor or the action command class
     * wrapped in an {@link ActionCommandConfigurer ActionCommandConfigurer}. All method
     * arguments will be ignored.
     */
    @Override
    protected UriActionCommandFactory interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                          String currentUriToken,
                                                          List<String> uriTokens,
                                                          Map<String, String> queryParameters,
                                                          ParameterMode parameterMode) {
         if (getActionCommandFactory() != null) {
            LOG.debug("interpretTokensImpl() - Returning action command factory {} for current URI token '{}'", getActionCommandFactory(), currentUriToken);
            return getActionCommandFactory();
        } else {
            LOG.debug("interpretTokensImpl() - No action command factory defined for current URI token '{}'", currentUriToken);
            return null;
        }
    }

    @Override
    public void getMapperOverview(String path, List<String> mapperOverviewList) {
        mapperOverviewList.add(String.format("%s/%s%s -> %s",
                path,
                getSegmentInfo(),
                getParameterListAsString(),
                actionInfo()));
    }
}
