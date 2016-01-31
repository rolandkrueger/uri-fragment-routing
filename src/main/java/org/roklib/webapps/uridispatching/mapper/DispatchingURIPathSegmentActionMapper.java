package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;

import java.util.*;

/**
 * Action mapper that dispatches to a set of sub-mappers. By this, this class is responsible for handling the inner
 * directories of a URI fragment.
 *
 * @author Roland Krüger
 */
public class DispatchingURIPathSegmentActionMapper extends AbstractURIPathSegmentActionMapper {
    private static final long serialVersionUID = - 777810072366030611L;

    private Map<String, AbstractURIPathSegmentActionMapper> subMappers;

    /**
     * Create a dispatching action mapper with the provided action name. The action name is the part of the URI that is
     * handled by this action mapper.
     *
     * @param mapperName
     *         the path segment name for this dispatching action mapper
     */
    public DispatchingURIPathSegmentActionMapper(String mapperName) {
        super(mapperName);
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
     * SimpleURIPathSegmentActionMapper}s that simply return an {@link URIActionCommand} when being evaluated. </p> <p>
     * The case sensitivity of this action mapper is inherited to the sub-mapper. </p>
     *
     * @param subMapper
     *         the sub-mapper to be added to this {@link DispatchingURIPathSegmentActionMapper}
     *
     * @throws IllegalArgumentException
     *         if the passed action mapper already has been added as sub-mapper to another {@link
     *         DispatchingURIPathSegmentActionMapper}. In other words, if the passed sub-mapper already has a parent
     *         mapper.
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

    @Override
    protected Class<? extends URIActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                    List<String> uriTokens,
                                                                    Map<String, List<String>> parameters,
                                                                    ParameterMode parameterMode) {
        String currentMapperName = "";
        while ("".equals(currentMapperName) && ! uriTokens.isEmpty()){
            currentMapperName = uriTokens.remove(0);
        }

        if (uriTokens.isEmpty() && "".equals(currentMapperName)) {
            return getActionCommand();
        }

        return forwardToSubHandler(capturedParameterValues, currentMapperName, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to forward handling of the remaining URI tokens to the specific sub-mapper which is responsible for the
     * given <code>currentMapperName</code>.
     *
     * @param capturedParameterValues
     *         map of parameter values which have not yet been consumed by any registered parameters
     * @param currentMapperName
     *         the name of the sub-mapper which is responsible for interpreting the remaining URI tokens
     * @param uriTokens
     *         the remaining URI tokens to be interpreted by the sub-tree of this dispatching mapper
     * @param parameters
     *         set of already consumed parameters
     * @param parameterMode
     *         current {@link org.roklib.webapps.uridispatching.mapper.URIPathSegmentActionMapper.ParameterMode} to be
     *         used
     *
     * @return the action command as provided by the sub-mapper or <code>null</code> if no responsible sub-mapper could
     * be found for the <code>currentMapperName</code>. The latter situation corresponds to a 404 NOT FOUND.
     */
    private Class<? extends URIActionCommand> forwardToSubHandler(CapturedParameterValuesImpl capturedParameterValues,
                                                                  String currentMapperName,
                                                                  List<String> uriTokens,
                                                                  Map<String, List<String>> parameters,
                                                                  ParameterMode parameterMode) {
        AbstractURIPathSegmentActionMapper subMapper = getResponsibleSubMapperForMapperName(currentMapperName);
        if (subMapper == null) {
            return null;
        }

        return subMapper.interpretTokens(capturedParameterValues, uriTokens, parameters, parameterMode);
    }

    /**
     * Tries to find the next action mapper in line which is responsible for handling the current URI token. If such a
     * mapper is found, the responsibility for interpreting the current URI is passed to this mapper. Note that a
     * specific precedence rule applies to the registered sub-mappers as described in the class description.
     *
     * @param currentMapperName
     *         the currently interpreted URI token
     *
     * @return {@link AbstractURIPathSegmentActionMapper} that is responsible for handling the current URI token or
     * <code>null</code> if no such mapper could be found.
     */
    private AbstractURIPathSegmentActionMapper getResponsibleSubMapperForMapperName(String currentMapperName) {
        String mapperName = isCaseSensitive() ? currentMapperName : currentMapperName.toLowerCase(Locale.getDefault());

        AbstractURIPathSegmentActionMapper responsibleSubMapper = getSubMapperMap().get(mapperName);
        if (responsibleSubMapper != null) {
            return responsibleSubMapper;
        }

        for (AbstractURIPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            if (subMapper.isResponsibleForToken(mapperName)) {
                return subMapper;
            }
        }
        return null;
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
            if (! isCaseSensitive()) {
                subMappers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            } else {
                subMappers = new HashMap<>(4);
            }
        }
        return subMappers;
    }
}
