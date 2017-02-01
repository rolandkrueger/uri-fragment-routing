package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Action mapper that dispatches to a set of sub-mappers. By this, this class is responsible for handling the inner
 * directories of a URI fragment.
 * <p>
 * A dispatching action mapper can have a arbitrary number of sub-mappers. A sub-mapper is another {@link
 * AbstractUriPathSegmentActionMapper} which has been added to the dispatching mapper with {@link
 * #addSubMapper(UriPathSegmentActionMapper)}.
 * <p>
 * If, for instance, this dispatching mapper is responsible for a path segment name <tt>admin</tt>, then it may have two
 * sub-mappers responsible for the path segments <tt>users</tt> and <tt>groups</tt>. In this scenario, the following two
 * different URI fragments can be handled by the corresponding URI action mapper tree:
 * <pre>
 *     /admin/users
 *     /admin/groups
 * </pre>
 */
public class DispatchingUriPathSegmentActionMapper extends AbstractUriPathSegmentActionMapper {
    private static final long serialVersionUID = -777810072366030611L;
    private static final Logger LOG = LoggerFactory.getLogger(DispatchingUriPathSegmentActionMapper.class);


    private Map<String, UriPathSegmentActionMapper> subMappers;
    private CatchAllUriPathSegmentActionMapper catchAllMapper;

    /**
     * Create a dispatching action mapper with the provided mapper name. The action name is the part of the URI that is
     * handled by this action mapper.
     *
     * @param mapperName the mapper name for this dispatching action mapper which is simultaneously used as path segment
     *                   name
     */
    public DispatchingUriPathSegmentActionMapper(final String mapperName) {
        super(mapperName);
    }

    /**
     * Create a dispatching action mapper with the provided mapper name and path segment name.
     *
     * @param mapperName  the mapper name for this dispatching action mapper
     * @param pathSegment the path segment name for this mapper
     */
    public DispatchingUriPathSegmentActionMapper(final String mapperName, final String pathSegment) {
        super(mapperName, pathSegment);
    }

    /**
     * Registers a sub-mapper to this {@link DispatchingUriPathSegmentActionMapper}. Sub-mappers form the links of the
     * URI interpretation chain in that each of them is responsible for interpreting one particular fragment of a URI.
     * <p>
     * For example, if a web application offers the following two valid URIs
     * <pre>
     * http://www.example.com/myapp#!articles/list
     * http://www.example.com/myapp#!articles/showArticle
     * </pre>
     * then the URI action mapper for fragment {@code articles} has to be a {@link DispatchingUriPathSegmentActionMapper}
     * since it needs two sub-mappers for {@code list} and {@code showArticle}. These two fragments may be handled by
     * {@link DispatchingUriPathSegmentActionMapper}s themselves if they in turn allow sub-directories in the URI
     * structure. They could also be {@link SimpleUriPathSegmentActionMapper}s that simply return an {@link
     * UriActionCommand} when being evaluated.
     * <p>
     * The case sensitivity of this action mapper is inherited to the sub-mapper.
     *
     * @param subMapper the sub-mapper to be added to this {@link DispatchingUriPathSegmentActionMapper}
     *
     * @throws IllegalArgumentException if the passed action mapper already has been added as sub-mapper to another
     *                                  {@link DispatchingUriPathSegmentActionMapper}. In other words, if the passed
     *                                  sub-mapper already has a parent mapper.
     */
    public final void addSubMapper(final UriPathSegmentActionMapper subMapper) {
        Preconditions.checkNotNull(subMapper);
        if (subMapper.getParentMapper() != null)
            throw new IllegalArgumentException(String.format("This sub-mapper instance has "
                            + "already been added to another action mapper. This mapper = '%s'; sub-mapper = '%s'", getMapperName(),
                    subMapper.getMapperName()));
        subMapper.setParentMapper(this);
        if (subMapper instanceof CatchAllUriPathSegmentActionMapper) {
            catchAllMapper = (CatchAllUriPathSegmentActionMapper) subMapper;
        } else {
            getSubMapperMap().put(subMapper.getMapperName(), subMapper);
        }
        registerSubMapperName(subMapper.getMapperName());
    }

    @Override
    protected UriActionCommandFactory interpretTokensImpl(final CapturedParameterValues capturedParameterValues,
                                                          final String currentUriToken,
                                                          final List<String> uriTokens,
                                                          final Map<String, String> queryParameters,
                                                          final ParameterMode parameterMode) {
        String nextMapperName = "";
        while ("".equals(nextMapperName) && !uriTokens.isEmpty()) {
            // ignore empty URI tokens
            nextMapperName = uriTokens.remove(0);
        }

        if (uriTokens.isEmpty() && "".equals(nextMapperName)) {
            if (LOG.isDebugEnabled()) {
                String resultInfo = "no action command class";
                if (getActionCommand() != null) {
                    resultInfo = "my action command " + getActionCommand();
                }
                if (getActionCommandFactory() != null) {
                    resultInfo = "my action command factory " + getActionCommandFactory();
                }
                LOG.debug("{}.interpretTokensImpl() - Reached fragment's last URI token '{}': returning {}", toString(),
                        currentUriToken, resultInfo);
            }
            return determineActionCommandFactory();
        }

        return forwardToSubHandler(capturedParameterValues, nextMapperName, uriTokens, queryParameters, parameterMode);
    }

    /**
     * Tries to forward handling of the remaining URI fragment tokens to the specific sub-mapper which is responsible
     * for the specified URI fragment token which is next in line.
     *
     * @param capturedParameterValues map of URI parameter values that have been found in the currently interpreted URI
     *                                fragment so far
     * @param nextUriToken            the next URI token from the list of currently interpreted tokens
     * @param uriTokens               the remaining URI tokens to be interpreted by the sub-tree of this dispatching
     *                                mapper
     * @param parameters              raw set of parameter name/value pairs extracted from the currently interpreted URI
     *                                fragment. These parameters still wait to be converted and consumed. They will
     *                                eventually end up in the given {@link CapturedParameterValues} object.
     * @param parameterMode           current {@link ParameterMode} to be used
     *
     * @return the action command as provided by the sub-mapper or {@code null} if no responsible sub-mapper could be
     * found for the next URI fragment token.
     */
    private UriActionCommandFactory forwardToSubHandler(final CapturedParameterValues capturedParameterValues,
                                                        final String nextUriToken,
                                                        final List<String> uriTokens,
                                                        final Map<String, String> parameters,
                                                        final ParameterMode parameterMode) {
        final UriPathSegmentActionMapper subMapper = getResponsibleSubMapperForMapperName(nextUriToken);
        if (subMapper == null) {
            LOG.debug("{}.forwardToSubHandler() - No sub mapper found for URI token '{}': Returning no action command class.", toString(), nextUriToken);
            return null;
        }

        LOG.debug("{}.forwardToSubHandler() - Forwarding to sub handler {}", toString(), subMapper);
        return subMapper.interpretTokens(capturedParameterValues, nextUriToken, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to find the next path segment action mapper in line which is responsible for handling the given URI token.
     * If such a mapper is found, the responsibility for interpreting the current URI is passed to this mapper. Note
     * that a specific precedence rule applies to the registered sub-mappers as described in the class description.
     *
     * @param nextUriToken the currently interpreted URI token
     *
     * @return the {@link UriPathSegmentActionMapper} that is responsible for handling the next URI token in line or
     * {@code null} if no such mapper could be found.
     */
    private UriPathSegmentActionMapper getResponsibleSubMapperForMapperName(final String nextUriToken) {
        final UriPathSegmentActionMapper responsibleSubMapper = getSubMapperMap().get(nextUriToken);
        if (responsibleSubMapper != null) {
            return responsibleSubMapper;
        }

        for (final UriPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            if (subMapper.isResponsibleForToken(nextUriToken)) {
                return subMapper;
            }
        }
        return catchAllMapper;
    }

    public Map<String, UriPathSegmentActionMapper> getSubMapperMap() {
        if (subMappers == null) {
            subMappers = new TreeMap<>();
        }
        return subMappers;
    }

    @Override
    public void getMapperOverview(final String path, final List<String> mapperOverviewList) {
        final String myPath = path + "/" + getSegmentInfo() + getParameterListAsString();
        if (getSubMapperMap().isEmpty() || getActionCommand() != null || getActionCommandFactory() != null) {
            mapperOverviewList.add(myPath + (actionInfo() == null ? "" : " -> " + actionInfo()));
        }
        getSubMapperMap()
                .values()
                .forEach(mapper -> mapper.getMapperOverview(myPath, mapperOverviewList));
        if (catchAllMapper != null) {
            catchAllMapper.getMapperOverview(myPath, mapperOverviewList);
        }
    }
}
