package org.roklib.webapps.uridispatching;

import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static org.roklib.webapps.uridispatching.mapper.URIPathSegmentActionMapper.ParameterMode;

/**
 * <p> The central dispatcher which provides the main entry point for the URI action handling framework. The action
 * dispatcher manages one internal root URI action mapper which dispatches to its sub-mappers. When a visited URI
 * fragment has to be interpreted, this URI fragment is passed to method {@link #handleURIAction(String)} or {@link
 * #handleURIAction(String, ParameterMode)}, respectively. There, the URI is split into a token list to be recursively
 * interpreted by the registered action mappers. For example, if the following URI is to be interpreted <p/>
 * <pre>
 * http://www.example.com/myapp#!user/home/messages
 * </pre>
 * <p/> with the web application installed under context <code>http://www.example.com/myapp/</code> the URI fragment to
 * be interpreted is <code>/user/home/messages</code>. This is split into three individual tokens <code>user</code>,
 * <code>home</code>, and <code>messages</code> in that order. To interpret these tokens, the root action mapper passes
 * them to the sub-mapper which has been registered as mapper for the first token <code>user</code>. If no such
 * mapper has been registered, the dispatcher will do nothing more or return the default action command that has been
 * registered with {@link #setDefaultAction(AbstractURIActionCommand)}. It thus indicates, that the URI could not
 * successfully be interpreted. </p> <p> Note that this class is not thread-safe, i.e. it must not be used to handle
 * access to several URIs in parallel. You should use one action dispatcher per HTTP session. </p>
 *
 * @author Roland Krüger
 */
public class URIActionDispatcher implements Serializable {
    private static final long serialVersionUID = 7151587763812706383L;
    private static final Logger LOG = LoggerFactory.getLogger(URIActionDispatcher.class);

    private final Map<String, List<String>> currentParameters;
    private String relativeUriOriginal;
    private Map<String, String[]> currentParametersOriginalValues;
    private AbstractURIActionCommand defaultAction;
    /**
     * Base dispatching mapper that contains all action mappers at root level.
     */
    private final DispatchingURIPathSegmentActionMapper rootMapper;
    private URIActionDispatcherListener listener;
    private ParameterMode parameterMode = ParameterMode.QUERY;

    public URIActionDispatcher(boolean useCaseSensitiveURIs /* TODO: remove this parameter */) {
        if (useCaseSensitiveURIs) {
            currentParameters = new HashMap<>();
        } else {
            currentParameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        rootMapper = new DispatchingURIPathSegmentActionMapper("");
        rootMapper.setCaseSensitive(useCaseSensitiveURIs);
        rootMapper.setParent(new AbstractURIPathSegmentActionMapper("") {
            private static final long serialVersionUID = 3744506992900879054L;

            protected AbstractURIActionCommand handleURIImpl(List<String> uriTokens, Map<String, List<String>> parameters,
                                                             ParameterMode parameterMode) {
                return null;
            }

            @Override
            protected boolean isResponsibleForToken(String uriToken) {
                throw new UnsupportedOperationException();
            }
        });
    }

    public boolean isCaseSensitive() {
        return rootMapper.isCaseSensitive();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        rootMapper.setCaseSensitive(caseSensitive);
    }

    /**
     * Returns the root dispatching mapper that is the entry point of the URI interpretation chain. This is a special
     * action mapper as the URI token it is responsible for (its <em>action name</em>) is the empty String. Thus, if a
     * visited URI is to be interpreted by this action dispatcher, this URI is first passed to that root dispatching
     * mapper. All URI action mappers that are responsible for the first directory level of a URI have to be added to
     * this root mapper as sub-mappers. To do that, you can also use the delegate method {@link
     * #addURIPathSegmentMapper(AbstractURIPathSegmentActionMapper)}.
     *
     * @return the root dispatching mapper for this action dispatcher
     * @see #addURIPathSegmentMapper(AbstractURIPathSegmentActionMapper)
     */
    public DispatchingURIPathSegmentActionMapper getRootActionMapper() {
        return rootMapper;
    }

    public void setURIActionDispatcherListener(URIActionDispatcherListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the action command to be executed each time when no responsible action mapper could be found for some
     * particular relative URI. If set to <code>null</code> no particular action is performed when an unknown relative
     * URI is handled.
     *
     * @param defaultAction command to be executed for an unknown relative URI, may be <code>null</code>
     */
    public void setDefaultAction(AbstractURIActionCommand defaultAction) {
        this.defaultAction = defaultAction;
    }

    /**
     * Returns the set of parameters that belong to the currently handled URI and have been set with {@link
     * #handleParameters(Map)}.
     *
     * @return
     */
    protected Map<String, List<String>> getParameters() {
        return currentParameters;
    }

    /**
     * Clears the set of parameter values that has been set with {@link #handleParameters(Map)}.
     */
    public void clearParameters() {
        currentParameters.clear();
    }

    public void handleParameters(Map<String, String[]> parameters) {
        if (parameters == null) {
            return;
        }
        currentParameters.clear();
        currentParametersOriginalValues = parameters;
        for (String key : parameters.keySet()) {
            List<String> params = new ArrayList<>(Arrays.asList(parameters.get(key)));
            if (!params.isEmpty()) {
                currentParameters.put(key, params);
            }
        }
    }

    /**
     * Set the parameter mode to be used for interpreting the visited URIs.
     *
     * @param parameterMode {@link ParameterMode} which will be used by {@link #handleURIAction(String)}
     */
    public void setParameterMode(ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

    /**
     * Passes the given relative URI to the URI action mapper chain and interprets all parameters with the {@link
     * ParameterMode} defined with {@link #setParameterMode(ParameterMode)}.
     *
     * @see #handleURIAction(String, ParameterMode)
     */
    // TODO: make package private (rewrite tests)
    public void handleURIAction(String relativeUri) {
        handleURIAction(relativeUri, parameterMode);
    }

    /**
     * This method is the central entry point for the URI action handling framework.
     *
     * @param relativeUri   relative URI to be interpreted by the URI action handling framework. This may be an URI such
     *                      as <code>/admin/configuration/settings/language/de</code>
     * @param parameterMode {@link ParameterMode} to be used for interpreting possible parameter values contained in the
     *                      given relative URI
     */
    // TODO: make package private (rewrite tests)
    public void handleURIAction(String relativeUri, ParameterMode parameterMode) {
        AbstractURIActionCommand action = getActionForURI(relativeUri, parameterMode);
        if (action == null) {
            LOG.info("No registered URI action mapper for: {}?{}", relativeUriOriginal, currentParameters);
            if (defaultAction != null) {
                defaultAction.execute();
            }
        } else {
            action.execute();
            if (listener != null) {
                listener.handleURIActionCommand(action);
            }
        }
    }

    private AbstractURIActionCommand getActionForURI(String relativeUri, ParameterMode parameterMode) {
        LOG.trace("Finding action for URI '{}'", relativeUri);
        relativeUriOriginal = removeLeadingSlash(relativeUri);
        List<String> uriTokens = new ArrayList<>(Arrays.asList(relativeUriOriginal.split("/")));
        LOG.trace("Dispatching URI: '{}', params: '{}'", relativeUriOriginal, currentParameters);

        return rootMapper.handleURI(uriTokens, currentParameters, parameterMode);
    }

    private String removeLeadingSlash(String relativeUri) {
        if (relativeUri.startsWith("/")) {
            return relativeUri.substring(1);
        }
        return relativeUri;
    }

    /**
     * Returns the relative URI that is currently being handled by this dispatcher. This URI is set each time {@link
     * #handleURIAction(String, ParameterMode)} is called.
     */
    public String getCurrentlyHandledURI() {
        return relativeUriOriginal;
    }

    /**
     * Adds a new mapper to the root action mapper of this dispatcher. For example, if this method is called three
     * times with action mappers for the fragments <code>admin</code>, <code>main</code>, and <code>login</code> on a
     * web application running in context <code>http://www.example.com/myapp</code> this dispatcher will be able to
     * interpret the following URIs:
     * <p/>
     * <pre>
     * http://www.example.com/myapp#!admin
     * http://www.example.com/myapp#!main
     * http://www.example.com/myapp#!login
     * </pre>
     *
     * @param subMapper the new action mapper to be added to the root level
     * @throws IllegalArgumentException if the given sub-mapper has already been added to another parent mapper
     */
    public final void addURIPathSegmentMapper(AbstractURIPathSegmentActionMapper subMapper) {
        getRootActionMapper().addSubMapper(subMapper);
    }
}
