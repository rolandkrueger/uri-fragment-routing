package org.roklib.urifragmentrouting;

import org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.DispatchingUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The central dispatcher which provides the main entry point for the URI action handling framework. The action
 * dispatcher manages one internal root URI action mapper which dispatches to its sub-mappers. When a visited URI
 * fragment has to be interpreted, this URI fragment is passed to method {@link #handleURIAction(String)} or {@link
 * #handleURIAction(String, ParameterMode)}, respectively. There, the URI is split into a token list to be recursively
 * interpreted by the registered action mappers. For example, if the following URI is to be interpreted
 * <pre>
 * http://www.example.com/myapp#!user/home/messages
 * </pre>
 * with the web application installed under context <code>http://www.example.com/myapp/</code> the URI fragment to be
 * interpreted is <code>/user/home/messages</code>. This is split into three individual tokens <code>user</code>,
 * <code>home</code>, and <code>messages</code> in that order. To interpret these tokens, the root action mapper passes
 * them to the sub-mapper which has been registered as mapper for the first token <code>user</code>. If no such mapper
 * has been registered, the dispatcher will do nothing more or return the default action command that has been
 * registered with {@link #setDefaultAction(Class)}. It thus indicates, that the URI could not successfully be
 * interpreted.
 * <p>
 * Note that this class is not thread-safe, i.e. it must not be used to handle access to several URIs in parallel. You
 * should use one action dispatcher per HTTP session.
 */
public class UriActionDispatcher implements Serializable {
    private static final long serialVersionUID = 7151587763812706383L;

    private static final Logger LOG = LoggerFactory.getLogger(UriActionDispatcher.class);

    private Class<? extends UriActionCommand> defaultAction;
    /**
     * Base dispatching mapper that contains all action mappers at root level.
     */
    private final DispatchingUriPathSegmentActionMapper rootMapper;
    /**
     * Set comprising the mapper names of all action mappers contained in this action dispatcher.
     */
    private Set<String> mapperNamesInUse;


    public UriActionDispatcher() {
        rootMapper = new DispatchingUriPathSegmentActionMapper("");
        rootMapper.setParentMapper(new AbstractUriPathSegmentActionMapper("") {
            private static final long serialVersionUID = 3744506992900879054L;

            protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                            String currentUriToken,
                                                                            List<String> uriTokens,
                                                                            Map<String, String> queryParameters,
                                                                            ParameterMode parameterMode) {
                return null;
            }

            @Override
            public boolean isResponsibleForToken(String uriToken) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void getMapperOverview(String path, List<String> mapperOverviewList) {
            }

            @Override
            public void registerSubMapperName(String subMapperName) {
                if (isMapperNameInUse(subMapperName)) {
                    throw new IllegalArgumentException("Mapper name '" + subMapperName + "' is already in use");
                }
                addUsedMapperName(subMapperName);
            }
        });
        mapperNamesInUse = new HashSet<>();
    }


    /**
     * Returns the root dispatching mapper that is the entry point of the URI interpretation chain. This is a special
     * action mapper as the URI token it is responsible for (its <em>action name</em>) is the empty String. Thus, if a
     * visited URI is to be interpreted by this action dispatcher, this URI is first passed to that root dispatching
     * mapper. All URI action mappers that are responsible for the first directory level of a URI have to be added to
     * this root mapper as sub-mappers.
     *
     * @return the root dispatching mapper for this action dispatcher
     */
    DispatchingUriPathSegmentActionMapper getRootActionMapper() {
        return rootMapper;
    }

    /**
     * Sets the action command to be executed each time when no responsible action mapper could be found for some
     * particular relative URI. If set to <code>null</code> no particular action is performed when an unknown relative
     * URI is handled.
     *
     * @param defaultAction command to be executed for an unknown relative URI, may be <code>null</code>
     */
    public void setDefaultAction(Class<? extends UriActionCommand> defaultAction) {
        this.defaultAction = defaultAction;
    }

    public Class<? extends UriActionCommand> getActionForUriFragment(CapturedParameterValues capturedParameterValues,
                                                                     String uriFragment,
                                                                     List<String> uriTokens,
                                                                     Map<String, String> extractedQueryParameters,
                                                                     ParameterMode parameterMode) {
        LOG.trace("Dispatching URI: '{}', params: '{}'", uriFragment, extractedQueryParameters);

        final Class<? extends UriActionCommand> action = rootMapper.interpretTokens(capturedParameterValues, null, uriTokens, extractedQueryParameters, parameterMode);

        if (action == null) {
            LOG.info("No registered URI action mapper for: {}", uriFragment);
            return defaultAction;
        }
        return action;
    }

    private boolean isMapperNameInUse(String mapperName) {
        return mapperNamesInUse.contains(mapperName);
    }

    private void addUsedMapperName(String mapperName) {
        mapperNamesInUse.add(mapperName);
    }
}
