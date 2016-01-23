package org.roklib.webapps.uridispatching.mapper;

import org.roklib.util.helper.CheckForNull;
import org.roklib.webapps.uridispatching.AbstractURIActionCommand;
import org.roklib.webapps.uridispatching.parameters.EnumURIParameterErrors;
import org.roklib.webapps.uridispatching.parameters.URIParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public abstract class AbstractURIPathSegmentActionMapper implements URIPathSegmentActionMapper {
    private static final long serialVersionUID = 8450975393827044559L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractURIPathSegmentActionMapper.class);

    private List<URIParameter<?>> uriParameters;
    private List<String> actionArgumentOrder;
    protected List<URIPathSegmentActionMapper> mapperChain;
    private Map<String, List<Serializable>> actionArgumentMap;
    protected AbstractURIPathSegmentActionMapper parentMapper;
    private AbstractURIActionCommand actionCommand;

    /**
     * The name of the URI portion for which this action mapper is responsible.
     */
    protected String actionName;
    private String actionURI;
    private boolean caseSensitive = false;
    private boolean useHashExclamationMarkNotation = false;
    private Locale locale;

    /**
     * Creates a new action mapper with the given action name. The action name must not be <code>null</code>. This name
     * identifies the fragment of a URI which is handled by this action mapper. For example, if this action mapper is
     * responsible for the <code>admin</code> part in the following URI
     * <p/>
     * <pre>
     * http://www.example.com/admin/settings
     * </pre>
     * <p/>
     * then the action name for this mapper has to be set to <code>admin</code> as well.
     *
     * @param segmentName the name of the URI path segment for which this action mapper is responsible. Must not be
     *                    <code>null</code>.
     */
    public AbstractURIPathSegmentActionMapper(String segmentName) {
        CheckForNull.check(segmentName);
        this.actionName = segmentName;
        actionURI = segmentName;
    }

    @Deprecated
    protected void setUseHashExclamationMarkNotation(boolean useHashExclamationMarkNotation) {
        this.useHashExclamationMarkNotation = useHashExclamationMarkNotation;
    }

    /**
     * <p> Sets the case sensitivity of this action mapper. A case insentitive action mapper will match a URI token
     * without regarding the token's case. You have to be careful with case insensitive action mappers if you have more
     * than one action mapper with action names differing only in case. You might get unexpected results since one
     * action mapper might shadow the other. </p>
     */
    protected void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getActionName() {
        return actionName;
    }

    public String getCaseInsensitiveActionName() {
        return actionName.toLowerCase(getLocale());
    }

    /**
     * Sets the action command for this action mapper. This is the given {@link AbstractURIActionCommand} which will be
     * returned when the token list to be interpreted by this mapper is empty. This is the case when a URI is being
     * interpreted that directly points to this {@link AbstractURIPathSegmentActionMapper}. For example, if the
     * following URI is passed to the URI action handling framework
     * <p/>
     * <pre>
     * http://www.example.com/myapp#!home/
     *                       \____/
     *                context path
     *                             \___/ URI path interpreted by the URI action framework
     * </pre>
     * <p/>
     * where the URI action mapper for token <code>home</code> is a sub-class of {@link
     * AbstractURIPathSegmentActionMapper}, then this mapper's action command is used as the outcome of the URI
     * interpretation. This command could then provide some logic for the interpreted URI, such as redirecting to the
     * correct home screen for the currently signed in user, or performing some other action.
     *
     * @param command action command to be used when interpreting a URI which points directly to this action mapper. Can
     *                be <code>null</code>.
     */
    public void setActionCommand(AbstractURIActionCommand command) {
        actionCommand = command;
    }

    public AbstractURIActionCommand getActionCommand() {
        return actionCommand;
    }

    protected void registerURIParameter(URIParameter<?> parameter) {
        if (parameter == null) {
            return;
        }
        if (uriParameters == null) {
            uriParameters = new LinkedList<>();
        }
        if (!uriParameters.contains(parameter)) {
            uriParameters.add(parameter);
        }
    }

    protected void registerURIParameter(URIParameter<?> parameter, boolean isOptional) {
        registerURIParameter(parameter);
        parameter.setOptional(isOptional);
    }

    protected boolean haveRegisteredURIParametersErrors() {
        if (uriParameters == null) {
            return false;
        }
        boolean result = false;

        for (URIParameter<?> parameter : uriParameters) {
            result |= parameter.getError() != EnumURIParameterErrors.NO_ERROR;
        }

        return result;
    }

    private List<URIParameter<?>>  getUriParameters(){
        return uriParameters == null ? Collections.emptyList() : uriParameters;
    }

    public final AbstractURIActionCommand handleURI(List<String> uriTokens, Map<String, List<String>> parameters,
                                                    ParameterMode parameterMode) {
        if (!getUriParameters().isEmpty()) {
            if (parameterMode == ParameterMode.QUERY) {
                consumeQueryParameters(parameters);
            } else {
                List<String> parameterNames = new LinkedList<>();
                for (URIParameter<?> parameter : uriParameters) {
                    parameterNames.addAll(parameter.getParameterNames());
                }
                if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                    Map<String, List<String>> parameterMap = new HashMap<>(4);
                    String parameterName;
                    String value;
                    for (Iterator<String> it = uriTokens.iterator(); it.hasNext(); ) {
                        parameterName = urlDecode(it.next());
                        value = "";
                        if (parameterNames.contains(parameterName)) {
                            it.remove();
                            if (it.hasNext()) {
                                value = urlDecode(it.next());
                                it.remove();
                            }
                            List<String> values = parameterMap.get(parameterName);
                            if (values == null) {
                                values = new LinkedList<>();
                                parameterMap.put(parameterName, values);
                            }
                            values.add(value);
                        }
                    }
                    consumeQueryParameters(parameterMap);
                } else if (parameterMode == ParameterMode.DIRECTORY) {
                    List<String> valueList = new LinkedList<>();
                    for (URIParameter<?> parameter : uriParameters) {
                        parameter.clearValue();
                        if (uriTokens.isEmpty()) {
                            continue;
                        }
                        valueList.clear();
                        int singleValueCount = parameter.getSingleValueCount();
                        int i = 0;
                        while (!uriTokens.isEmpty() && i < singleValueCount) {
                            String token = urlDecode(uriTokens.remove(0));
                            valueList.add(token);
                            ++i;
                        }
                        parameter.consumeList(valueList.toArray(new String[valueList.size()]));
                    }
                }
            }
        }

        if (mapperChain != null) {
            for (URIPathSegmentActionMapper chainedMapper : mapperChain) {
                LOG.trace("Executing chained mapper {} ({} chained mapper(s) in list)", chainedMapper, mapperChain.size());
                AbstractURIActionCommand commandFromChain = chainedMapper.handleURI(uriTokens, parameters, parameterMode);
                if (commandFromChain != null) {
                    return commandFromChain;
                }
            }
        }

        return handleURIImpl(uriTokens, parameters, parameterMode);
    }

    private void consumeQueryParameters(Map<String, List<String>> parameters) {
        for (URIParameter<?> parameter : uriParameters) {
            parameter.clearValue();
            parameter.consume(parameters);
        }
    }

    protected abstract AbstractURIActionCommand handleURIImpl(List<String> uriTokens,
                                                              Map<String, List<String>> parameters,
                                                              ParameterMode parameterMode);

    protected boolean isResponsibleForToken(String uriToken) {
        if (isCaseSensitive()) {
            return actionName.equals(uriToken);
        } else {
            return actionName.equalsIgnoreCase(uriToken);
        }
    }

    protected String urlEncode(String term) {
        try {
            return URLEncoder.encode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding not supported on this platform");
        }
    }

    private String urlDecode(String term) {
        try {
            return URLDecoder.decode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding not supported on this platform");
        }
    }

    /**
     * Returns the full relative action URI for this action mapper. This is the concatenation of all parent mapper
     * action names going back to the mapper root separated by a slash. For example, if this action mapper's action name
     * is <code>languageSettings</code>, with its parent's action name <code>configuration</code> and the next parent's
     * action name <code>admin</code> then the action URI for this mapper evaluates to
     * <p/>
     * <pre>
     * /admin/configuration/languageSettings.
     * </pre>
     * <p/>
     * This String is needed for generating fully configured URIs (this URI together with the corresponding parameter
     * values) which can be used for rendering links pointing to this action mapper.
     *
     * @return the action URI for this action mapper (such as <code>/admin/configuration/languageSettings</code> if this
     * action mapper's action name is <code>languageSettings</code>).
     */
    public String getActionURI() {
        return actionURI;
    }

    /**
     * Sets the parent action mapper for this object. An action mapper can only be added as sub-mapper to one action
     * mapper. In other words, an action mapper can only have one parent.
     *
     * @param parent the parent mapper for this action mapper
     */
    public final void setParent(AbstractURIPathSegmentActionMapper parent) {
        parentMapper = parent;
    }

    public URI getParameterizedHashbangActionURI(boolean clearParametersAfterwards) {
        return getParameterizedHashbangActionURI(clearParametersAfterwards, ParameterMode.DIRECTORY_WITH_NAMES);
    }

    public URI getParameterizedHashbangActionURI(boolean clearParametersAfterwards, ParameterMode parameterMode) {
        return getParameterizedActionURI(clearParametersAfterwards, parameterMode, true, true);
    }

    public URI getParameterizedActionURI(boolean clearParametersAfterwards) {
        return getParameterizedActionURI(clearParametersAfterwards, ParameterMode.QUERY);
    }

    public URI getParameterizedActionURI(boolean clearParametersAfterwards, ParameterMode parameterMode) {
        return getParameterizedActionURI(clearParametersAfterwards, parameterMode, false);
    }

    public URI getParameterizedActionURI(boolean clearParametersAfterwards, ParameterMode parameterMode,
                                         boolean addHashMark) {
        return getParameterizedActionURI(clearParametersAfterwards, parameterMode, addHashMark,
            useHashExclamationMarkNotation);
    }

    private URI getParameterizedActionURI(boolean clearParametersAfterwards, ParameterMode parameterMode,
                                          boolean addHashMark, boolean addExclamationMark) {
        StringBuilder buf = new StringBuilder();
        if (addHashMark) {
            buf.append('#');
            if (addExclamationMark) {
                buf.append('!');
            }
            buf.append(getActionURI().substring(1));
        } else {
            buf.append(getActionURI());
        }

        boolean removeLastCharacter = false;
        if (actionArgumentMap != null && !actionArgumentMap.isEmpty()) {
            if (parameterMode == ParameterMode.QUERY) {
                buf.append('?');
                for (String argument : actionArgumentOrder) {
                    for (Serializable value : actionArgumentMap.get(argument)) {
                        buf.append(urlEncode(argument)).append('=').append(urlEncode(value.toString()));
                        buf.append('&');
                        removeLastCharacter = true;
                    }
                }
            } else {
                buf.append('/');
                for (String argument : actionArgumentOrder) {
                    for (Serializable value : actionArgumentMap.get(argument)) {
                        if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                            buf.append(urlEncode(argument)).append('/');
                        }
                        buf.append(urlEncode(value.toString()));
                        buf.append('/');
                        removeLastCharacter = true;
                    }
                }
            }
        }

        if (removeLastCharacter) {
            buf.setLength(buf.length() - 1);
        }

        try {
            return new URI(buf.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to create URL object.", e);
        } finally {
            if (clearParametersAfterwards) {
                clearActionArguments();
            }
        }
    }

    public void addToMapperChain(URIPathSegmentActionMapper mapper) {
        CheckForNull.check(mapper);
        if (mapperChain == null) {
            mapperChain = new LinkedList<>();
        }
        mapperChain.add(mapper);
    }

    /**
     * <code>null</code> argument values are ignored.
     */
    public void addActionArgument(String argumentName, Serializable... argumentValues) {
        CheckForNull.check(argumentName);
        if (actionArgumentMap == null) {
            actionArgumentMap = new HashMap<>(4);
            actionArgumentOrder = new LinkedList<>();
        }

        List<Serializable> valueList = actionArgumentMap.get(argumentName);
        if (valueList == null) {
            valueList = new LinkedList<>();
            actionArgumentMap.put(argumentName, valueList);
        }
        for (Serializable value : argumentValues) {
            if (value != null) {
                valueList.add(value);
            }
        }
        if (valueList.isEmpty()) {
            actionArgumentMap.remove(argumentName);
        } else if (!actionArgumentOrder.contains(argumentName)) {
            actionArgumentOrder.add(argumentName);
        }
    }

    public void clearActionArguments() {
        if (actionArgumentMap != null) {
            actionArgumentMap.clear();
            actionArgumentOrder.clear();
        }
    }

    public void getActionURIOverview(List<String> targetList) {
        StringBuilder buf = new StringBuilder();
        buf.append(getActionURI());

        if (uriParameters != null && uriParameters.size() > 0) {
            buf.append(" ? ");
            for (URIParameter<?> parameter : uriParameters) {
                buf.append(parameter).append(", ");
            }
            buf.setLength(buf.length() - 2);
        }
        if (buf.length() > 0) {
            targetList.add(buf.toString());
        }
        for (AbstractURIPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            subMapper.getActionURIOverview(targetList);
        }
    }

    /**
     * Returns a map of all registered sub-mappers for this URI action mapper. This method is only implemented by {@link
     * DispatchingURIPathSegmentActionMapper} since this is the only URI action mapper implementation in the framework
     * which can have sub-mappers. All other subclasses of {@link AbstractURIPathSegmentActionMapper} return an empty
     * map.
     *
     * @return map containing a mapping of URI tokens on the corresponding sub-mappers that handle these tokens.
     */
    public Map<String, AbstractURIPathSegmentActionMapper> getSubMapperMap() {
        return Collections.emptyMap();
    }

    public boolean hasSubMappers() {
        return !getSubMapperMap().isEmpty();
    }

    protected void setSubMappersActionURI(AbstractURIPathSegmentActionMapper subMapper) {
        subMapper.setActionURI(String.format("%s%s%s", getActionURI(), "/", urlEncode(subMapper.actionName)));
        if (subMapper.hasSubMappers()) {
            subMapper.updateActionURIs();
        }
    }

    protected void updateActionURIs() {
        setActionURI(parentMapper.getActionURI() + "/" + actionName);
        for (AbstractURIPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            setSubMappersActionURI(subMapper);
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }

    protected void setActionURI(String actionURI) {
        this.actionURI = actionURI;
    }

    @Override
    public String toString() {
        return String.format("%s='%s'", getClass().getSimpleName(), actionName);
    }
}