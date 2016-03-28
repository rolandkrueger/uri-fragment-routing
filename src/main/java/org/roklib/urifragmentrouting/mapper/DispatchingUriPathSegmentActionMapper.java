package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Action mapper that dispatches to a set of sub-mappers. By this, this class is responsible for handling the inner
 * directories of a URI fragment.
 *
 * @author Roland Krüger
 */
public class DispatchingUriPathSegmentActionMapper extends AbstractUriPathSegmentActionMapper {
    private static final long serialVersionUID = -777810072366030611L;

    private Map<String, AbstractUriPathSegmentActionMapper> subMappers;

    /**
     * Create a dispatching action mapper with the provided action name. The action name is the part of the URI that is
     * handled by this action mapper.
     *
     * @param mapperName the path segment name for this dispatching action mapper
     */
    public DispatchingUriPathSegmentActionMapper(String mapperName) {
        super(mapperName);
    }

    /**
     * <p> Registers a sub-mapper to this {@link DispatchingUriPathSegmentActionMapper}. Sub-mappers form the links of
     * the URI interpretation chain in that each of them is responsible for interpreting one particular fragment of a
     * URI. </p> <p> For example, if a web application offers the following two valid URIs <p/>
     * <pre>
     * http://www.example.com/myapp#!articles/list
     * http://www.example.com/myapp#!articles/showArticle
     * </pre>
     * <p/> then the URI action mapper for fragment <code>articles</code> has to be a {@link
     * DispatchingUriPathSegmentActionMapper} since it needs two sub-mappers for <code>list</code> and
     * <code>showArticle</code>. These two fragments may be handled by {@link DispatchingUriPathSegmentActionMapper}s
     * themselves if they in turn allow sub-directories in the URI structure. They could also be {@link
     * SimpleUriPathSegmentActionMapper}s that simply return an {@link UriActionCommand} when being evaluated. </p> <p>
     * The case sensitivity of this action mapper is inherited to the sub-mapper. </p>
     *
     * @param subMapper the sub-mapper to be added to this {@link DispatchingUriPathSegmentActionMapper}
     * @throws IllegalArgumentException if the passed action mapper already has been added as sub-mapper to another
     *                                  {@link DispatchingUriPathSegmentActionMapper}. In other words, if the passed
     *                                  sub-mapper already has a parent mapper.
     */
    public final void addSubMapper(AbstractUriPathSegmentActionMapper subMapper) {
        Preconditions.checkNotNull(subMapper);
        if (subMapper.parentMapper != null)
            throw new IllegalArgumentException(String.format("This sub-mapper instance has "
                            + "already been added to another action mapper. This mapper = '%s'; sub-mapper = '%s'", mapperName,
                    subMapper.mapperName));
        subMapper.parentMapper = this;
        getSubMapperMap().put(subMapper.mapperName, subMapper);
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> parameters,
                                                                    ParameterMode parameterMode) {
        String nextMapperName = "";
        while ("".equals(nextMapperName) && !uriTokens.isEmpty()) {
            // ignore empty URI tokens
            nextMapperName = uriTokens.remove(0);
        }

        if (uriTokens.isEmpty() && "".equals(nextMapperName)) {
            return getActionCommand();
        }

        return forwardToSubHandler(capturedParameterValues, nextMapperName, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to forward handling of the remaining URI tokens to the specific sub-mapper which is responsible for the
     * given <code>nextMapperName</code>.
     *
     * @param capturedParameterValues map of parameter values which have not yet been consumed by any registered
     *                                parameters
     * @param nextMapperName          the name of the sub-mapper which is responsible for interpreting the remaining URI
     *                                tokens
     * @param uriTokens               the remaining URI tokens to be interpreted by the sub-tree of this dispatching
     *                                mapper
     * @param parameters              set of already consumed parameters
     * @param parameterMode           current {@link ParameterMode} to be used
     * @return the action command as provided by the sub-mapper or <code>null</code> if no responsible sub-mapper could
     * be found for the <code>nextMapperName</code>. The latter situation corresponds to a 404 NOT FOUND.
     */
    private Class<? extends UriActionCommand> forwardToSubHandler(CapturedParameterValues capturedParameterValues,
                                                                  String nextMapperName,
                                                                  List<String> uriTokens,
                                                                  Map<String, String> parameters,
                                                                  ParameterMode parameterMode) {
        AbstractUriPathSegmentActionMapper subMapper = getResponsibleSubMapperForMapperName(nextMapperName);
        if (subMapper == null) {
            return null;
        }

        return subMapper.interpretTokens(capturedParameterValues, nextMapperName, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to find the next action mapper in line which is responsible for handling the current URI token. If such a
     * mapper is found, the responsibility for interpreting the current URI is passed to this mapper. Note that a
     * specific precedence rule applies to the registered sub-mappers as described in the class description.
     *
     * @param nextMapperName the currently interpreted URI token
     * @return {@link AbstractUriPathSegmentActionMapper} that is responsible for handling the current URI token or
     * <code>null</code> if no such mapper could be found.
     */
    private AbstractUriPathSegmentActionMapper getResponsibleSubMapperForMapperName(String nextMapperName) {
        String mapperName = nextMapperName;

        AbstractUriPathSegmentActionMapper responsibleSubMapper = getSubMapperMap().get(mapperName);
        if (responsibleSubMapper != null) {
            return responsibleSubMapper;
        }

        for (AbstractUriPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            if (subMapper.isResponsibleForToken(mapperName)) {
                return subMapper;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, AbstractUriPathSegmentActionMapper> getSubMapperMap() {
        if (subMappers == null) {
            subMappers = new TreeMap<>();
        }
        return subMappers;
    }

    @Override
    public void getMapperOverview(String path, List<String> mapperOverviewList) {
        getSubMapperMap()
                .values()
                .stream()
                .forEach(mapper -> mapper.getMapperOverview(path + "/" + getMapperName() + getParameterListAsString(), mapperOverviewList));
    }
}
