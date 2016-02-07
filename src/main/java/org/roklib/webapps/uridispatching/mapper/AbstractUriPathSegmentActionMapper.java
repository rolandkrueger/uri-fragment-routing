package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.UriParameter;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValues;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.roklib.webapps.uridispatching.helper.UriEncoderDecoder.encodeUriFragment;

public abstract class AbstractUriPathSegmentActionMapper implements UriPathSegmentActionMapper {
    private static final long serialVersionUID = 8450975393827044559L;

    private Map<String, UriParameter<?>> registeredUriParameters;
    private Set<String> registeredUriParameterNames;

    private List<String> actionArgumentOrder;
    private Map<String, List<Serializable>> actionArgumentMap;
    protected AbstractUriPathSegmentActionMapper parentMapper;
    private Class<? extends UriActionCommand> actionCommand;

    /**
     * The name of the URI portion for which this action mapper is responsible.
     */
    protected String mapperName;
    private String actionURI;

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
     * @param mapperName the name of the URI path segment for which this action mapper is responsible. Must not be
     *                   <code>null</code>.
     */
    public AbstractUriPathSegmentActionMapper(String mapperName) {
        Preconditions.checkNotNull(mapperName);
        this.mapperName = mapperName;
        actionURI = mapperName;
    }

    public String getMapperName() {
        return mapperName;
    }

    /**
     * Sets the action command for this action mapper. This is the given {@link UriActionCommand} which will be returned
     * when the token list to be interpreted by this mapper is empty. This is the case when a URI is being interpreted
     * that directly points to this {@link AbstractUriPathSegmentActionMapper}. For example, if the following URI is
     * passed to the URI action handling framework
     * <p/>
     * <pre>
     * http://www.example.com/myapp#!home/
     *                       \____/
     *                context path
     *                             \___/ URI path interpreted by the URI action framework
     * </pre>
     * <p/>
     * where the URI action mapper for token <code>home</code> is a sub-class of {@link
     * AbstractUriPathSegmentActionMapper}, then this mapper's action command is used as the outcome of the URI
     * interpretation. This command could then provide some logic for the interpreted URI, such as redirecting to the
     * correct home screen for the currently signed in user, or performing some other action.
     *
     * @param command action command to be used when interpreting a URI which points directly to this action mapper. Can be
     *                <code>null</code>.
     */
    public void setActionCommandClass(Class<? extends UriActionCommand> command) {
        actionCommand = command;
    }

    public Class<? extends UriActionCommand> getActionCommand() {
        return actionCommand;
    }

    public void registerURIParameter(UriParameter<?> parameter) {
        Preconditions.checkNotNull(parameter);

        if (registeredUriParameters == null) {
            registeredUriParameters = new LinkedHashMap<>();
            registeredUriParameterNames = new HashSet<>();
        }

        if (registeredUriParameters.containsKey(parameter.getId())) {
            throw new IllegalArgumentException("Another parameter with the same id is already registered on this mapper.");
        }

        parameter.getParameterNames()
                .stream()
                .forEach(parameterName -> {
                    if (registeredUriParameterNames.contains(parameterName)) {
                        throw new IllegalArgumentException("Cannot register parameter " + parameter +
                                ". Another parameter with parameter name '" + parameterName +
                                "' is already registered on this mapper.");
                    }
                    registeredUriParameters.put(parameter.getId(), parameter);
                    registeredUriParameterNames.add(parameterName);
                });
    }

    protected Map<String, UriParameter<?>> getUriParameters() {
        return registeredUriParameters == null ? Collections.emptyMap() : registeredUriParameters;
    }

    protected Set<String> getUriParameterNames() {
        return registeredUriParameterNames == null ? Collections.emptySet() : registeredUriParameterNames;
    }

    public final Class<? extends UriActionCommand> interpretTokens(CapturedParameterValuesImpl capturedParameterValues,
                                                                   String currentMapperName,
                                                                   List<String> uriTokens,
                                                                   Map<String, List<String>> queryParameters,
                                                                   ParameterMode parameterMode) {
        if (!getUriParameters().isEmpty()) {
            ParameterInterpreter interpreter = new ParameterInterpreter(mapperName);
            if (parameterMode == ParameterMode.QUERY) {
                interpreter.interpretQueryParameters(getUriParameters(), capturedParameterValues, queryParameters);
            } else {
                if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                    interpreter.interpretDirectoryParameters(getUriParameterNames(),
                            getUriParameters(),
                            capturedParameterValues,
                            uriTokens);
                } else if (parameterMode == ParameterMode.DIRECTORY) {
                    interpreter.interpretNamelessDirectoryParameters(getUriParameters(), capturedParameterValues, uriTokens);
                }
            }
        }

        return interpretTokensImpl(capturedParameterValues, currentMapperName, uriTokens, queryParameters, parameterMode);
    }

    protected abstract Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                             String currentMapperName,
                                                                             List<String> uriTokens,
                                                                             Map<String, List<String>> parameters,
                                                                             ParameterMode parameterMode);

    protected boolean isResponsibleForToken(String uriToken) {
        return mapperName.equals(uriToken);
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
    public final void setParent(AbstractUriPathSegmentActionMapper parent) {
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
                false);
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
                        buf.append(encodeUriFragment(argument)).append('=').append(encodeUriFragment(value.toString()));
                        buf.append('&');
                        removeLastCharacter = true;
                    }
                }
            } else {
                buf.append('/');
                for (String argument : actionArgumentOrder) {
                    for (Serializable value : actionArgumentMap.get(argument)) {
                        if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
                            buf.append(encodeUriFragment(argument)).append('/');
                        }
                        buf.append(encodeUriFragment(value.toString()));
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

    /**
     * <code>null</code> argument values are ignored.
     */
    public void addActionArgument(String argumentName, Serializable... argumentValues) {
        Preconditions.checkNotNull(argumentName);
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

        StringJoiner joiner = new StringJoiner(", ", " ? ", "");
        getUriParameters().values().stream().forEach(uriParameter -> joiner.add(uriParameter.toString()));
        buf.append(joiner.toString());

        if (buf.length() > 0) {
            targetList.add(buf.toString());
        }
        for (AbstractUriPathSegmentActionMapper subMapper : getSubMapperMap().values()) {
            subMapper.getActionURIOverview(targetList);
        }
    }

    /**
     * Returns a map of all registered sub-mappers for this URI action mapper. This method is only implemented by {@link
     * DispatchingUriPathSegmentActionMapper} since this is the only URI action mapper implementation in the framework
     * which can have sub-mappers. All other subclasses of {@link AbstractUriPathSegmentActionMapper} return an empty
     * map.
     *
     * @return map containing a mapping of URI tokens on the corresponding sub-mappers that handle these tokens.
     */
    protected Map<String, AbstractUriPathSegmentActionMapper> getSubMapperMap() {
        return Collections.emptyMap();
    }

    private boolean hasSubMappers() {
        return !getSubMapperMap().isEmpty();
    }

    protected void setSubMappersActionURI(AbstractUriPathSegmentActionMapper subMapper) {
        subMapper.setActionURI(String.format("%s%s%s", getActionURI(), "/", encodeUriFragment(subMapper.mapperName)));
        if (subMapper.hasSubMappers()) {
            subMapper.updateActionURIs();
        }
    }

    protected void updateActionURIs() {
        setActionURI(parentMapper.getActionURI() + "/" + mapperName);
        getSubMapperMap().values().forEach(this::setSubMappersActionURI);
    }

    protected void setActionURI(String actionURI) {
        this.actionURI = actionURI;
    }

    @Override
    public String toString() {
        return String.format("[%s='%s']", getClass().getSimpleName(), mapperName);
    }

    /**
     * Inner helper class for interpreting parameter values.
     */
    protected static class ParameterInterpreter implements Serializable {
        private String mapperName;

        public ParameterInterpreter(String mapperName) {
            this.mapperName = mapperName;
        }

        public CapturedParameterValues interpretDirectoryParameters(Set<String> registeredUriParameterNames,
                                                                    Map<String, UriParameter<?>> registeredUriParameters,
                                                                    CapturedParameterValuesImpl consumedValues,
                                                                    List<String> uriTokens) {
            Map<String, List<String>> directoryBasedParameterMap = new HashMap<>(4);
            for (Iterator<String> it = uriTokens.iterator(); it.hasNext(); ) {
                String parameterName = it.next();
                if (registeredUriParameterNames.contains(parameterName)) {
                    it.remove();

                    if (it.hasNext()) {
                        List<String> values = directoryBasedParameterMap.computeIfAbsent(parameterName, k -> new
                                LinkedList<>());
                        values.add(it.next());
                        it.remove();
                    }
                } else {
                    break;
                }
            }
            return interpretQueryParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        public CapturedParameterValues interpretNamelessDirectoryParameters(Map<String, UriParameter<?>> registeredUriParameters,
                                                                            CapturedParameterValuesImpl consumedValues,
                                                                            List<String> uriTokens) {
            Map<String, List<String>> directoryBasedParameterMap = new HashMap<>(4);
            outerLoop:
            for (UriParameter<?> parameter : registeredUriParameters.values()) {
                for (String parameterName : parameter.getParameterNames()) {
                    directoryBasedParameterMap.put(parameterName,
                            Collections.singletonList(uriTokens.remove(0)));
                    if (uriTokens.isEmpty()) {
                        break outerLoop;
                    }
                }
            }

            return interpretQueryParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        public CapturedParameterValues interpretQueryParameters(Map<String, UriParameter<?>> registeredUriParameters,
                                                                CapturedParameterValuesImpl capturedParameterValues,
                                                                Map<String, List<String>> queryParameters) {
            registeredUriParameters
                    .values()
                    .stream()
                    .forEach(parameter -> {
                        final ParameterValue<?> consumedParameterValue = parameter.consumeParameters(queryParameters);
                        if (consumedParameterValue != null) {
                            parameter.getParameterNames().stream().forEach(queryParameters::remove);
                        }
                        capturedParameterValues.setValueFor(mapperName, parameter, consumedParameterValue);
                    });

            return capturedParameterValues;
        }
    }
}
