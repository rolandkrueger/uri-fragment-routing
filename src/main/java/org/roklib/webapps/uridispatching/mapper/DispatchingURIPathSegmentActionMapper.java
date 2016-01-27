package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.value.ConsumedParameterValues;

import java.util.*;

/**
 * Action mapper that dispatches to a set of sub-mappers. By this, this class is responsible for handling the inner
 * directories of a URI fragment.
 *
 * @author Roland Krüger
 */
public class DispatchingURIPathSegmentActionMapper extends AbstractURIPathSegmentActionMapper {
    private static final long serialVersionUID = -777810072366030611L;

    private Class<? extends URIActionCommand> missingSubMapperCommand;
    private Map<String, AbstractURIPathSegmentActionMapper> subMappers;

    /**
     * Create a dispatching action mapper with the provided action name. The action name is the part of the URI that is
     * handled by this action mapper.
     *
     * @param segmentName the path segment name for this dispatching action mapper
     */
    public DispatchingURIPathSegmentActionMapper(String segmentName) {
        super(segmentName);
    }

    public void setMissingSubMapperCommand(Class<? extends URIActionCommand> missingSubMapperCommand) {
        this.missingSubMapperCommand = missingSubMapperCommand;
    }

    @Override
    protected Class<? extends URIActionCommand> interpretTokensImpl(ConsumedParameterValues consumedParameterValues,
                                                                    List<String> uriTokens,
                                                                    Map<String, List<String>> parameters,
                                                                    ParameterMode parameterMode) {
        if (noMoreTokensAvailable(uriTokens)) {
            return getActionCommand();
        }
        String currentActionName = uriTokens.remove(0);
        return forwardToSubHandler(consumedParameterValues, currentActionName, uriTokens, parameters, parameterMode);
    }

    private boolean noMoreTokensAvailable(final List<String> uriTokens) {
        return uriTokens.isEmpty() || "".equals(uriTokens.get(0));
    }

    private Class<? extends URIActionCommand> forwardToSubHandler(ConsumedParameterValues consumedParameterValues, String currentActionName, List<String> uriTokens,
                                                 Map<String, List<String>> parameters, ParameterMode parameterMode) {
        AbstractURIPathSegmentActionMapper subMapper = getResponsibleSubMapperForActionName(currentActionName);
        if (subMapper == null) {
            return missingSubMapperCommand;
        }

        return subMapper.interpretTokens(consumedParameterValues, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to find the next action mapper in line which is responsible for handling the current URI token. If such a
     * mapper is found, the responsibility for interpreting the current URI is passed to this mapper. Note that a
     * specific precedence rule applies to the registered sub-mappers as described in the class description.
     *
     * @param currentActionName the currently interpreted URI token
     * @return {@link AbstractURIPathSegmentActionMapper} that is responsible for handling the current URI token or
     * <code>null</code> if no such mapper could be found.
     */
    private AbstractURIPathSegmentActionMapper getResponsibleSubMapperForActionName(String currentActionName) {
        String actionName = isCaseSensitive() ? currentActionName : currentActionName.toLowerCase(Locale.getDefault());

        AbstractURIPathSegmentActionMapper responsibleSubMapper = getSubMapperMap().get(actionName);
        if (responsibleSubMapper != null) {
            return responsibleSubMapper;
        }

        for (AbstractURIPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            if (subMapper.isResponsibleForToken(actionName)) {
                return subMapper;
            }
        }
        return null;
    }

    /**
     * <p> Registers a sub-mapper to this {@link DispatchingURIPathSegmentActionMapper}. Sub-mappers form the links of
     * the URI interpretation chain in that each of them is responsible for interpreting one particular fragment of a
     * URI. </p> <p> For example, if a web application offers the following two valid URIs <p/>
     * <pre>
     * http://www.example.com/myapp#!articles/list
     * http://www.example.com/myapp#!articles/showArticle
     * </pre>
     * <p/> then the URI action mapper for fragment <code>articles</code> has to be a {@link
     * DispatchingURIPathSegmentActionMapper} since it needs two sub-mappers for <code>list</code> and
     * <code>showArticle</code>. These two fragments may be handled by {@link DispatchingURIPathSegmentActionMapper}s
     * themselves if they in turn allow sub-directories in the URI structure. They could also be {@link
     * SimpleURIPathSegmentActionMapper}s that simply return an {@link URIActionCommand} when being evaluated.
     * </p> <p> The case sensitivity of this action mapper is inherited to the sub-mapper. </p>
     *
     * @param subMapper the sub-mapper to be added to this {@link DispatchingURIPathSegmentActionMapper}
     * @throws IllegalArgumentException if the passed action mapper already has been added as sub-mapper to another
     *                                  {@link DispatchingURIPathSegmentActionMapper}. In other words, if the passed
     *                                  sub-mapper already has a parent mapper.
     */
    public final void addSubMapper(AbstractURIPathSegmentActionMapper subMapper) {
        Preconditions.checkNotNull(subMapper);
        if (subMapper.parentMapper != null)
            throw new IllegalArgumentException(String.format("This sub-mapper instance has "
                    + "already been added to another action mapper. This mapper = '%s'; sub-mapper = '%s'", mapperName,
                subMapper.mapperName));
        subMapper.parentMapper = this;
        setSubMappersActionURI(subMapper);
        getSubMapperMap().put(subMapper.mapperName, subMapper);
        subMapper.setCaseSensitive(isCaseSensitive());
    }

    /**
     * <p> {@inheritDoc} </p>
     */
    @Override
    public void setCaseSensitive(boolean caseSensitive) {
        super.setCaseSensitive(caseSensitive);
        if (isCaseSensitive() & subMappers != null) {
            rebuildSubMapperMap(caseSensitive);
            for (AbstractURIPathSegmentActionMapper subMapper : subMappers.values()) {
                subMapper.setCaseSensitive(caseSensitive);
            }
        }
    }

    private void rebuildSubMapperMap(boolean caseSensitive) {
        Map<String, AbstractURIPathSegmentActionMapper> subMappers = this.subMappers;
        this.subMappers = null;
        this.subMappers = getSubMapperMap();

        for (AbstractURIPathSegmentActionMapper subMapper : subMappers.values()) {
            String actionName = caseSensitive ? subMapper.getMapperName() : subMapper.getCaseInsensitiveActionName();
            this.subMappers.put(actionName, subMapper);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, AbstractURIPathSegmentActionMapper> getSubMapperMap() {
        if (subMappers == null) {
            if (!isCaseSensitive()) {
                subMappers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            } else {
                subMappers = new HashMap<>(4);
            }
        }
        return subMappers;
    }
}
