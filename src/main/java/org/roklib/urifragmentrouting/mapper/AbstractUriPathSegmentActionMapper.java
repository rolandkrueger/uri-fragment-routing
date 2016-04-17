package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.io.Serializable;
import java.util.*;

/**
 * Abstract default implementation for interface {@link UriPathSegmentActionMapper}.
 */
public abstract class AbstractUriPathSegmentActionMapper implements UriPathSegmentActionMapper {
    private static final long serialVersionUID = 8450975393827044559L;

    private Map<String, UriParameter<?>> registeredUriParameters;
    private Set<String> registeredUriParameterNames;
    private UriPathSegmentActionMapper parentMapper;
    private Class<? extends UriActionCommand> actionCommand;
    private final String mapperName;
    private final String pathSegment;

    /**
     * Creates a new action mapper with the given mapper name. The mapper name must not be null. This name identifies
     * the fragment of a URI which is handled by this action mapper. For example, if this action mapper is responsible
     * for the <code>admin</code> part in the following URI
     * <p/>
     * <pre>
     * http://www.example.com/app#!admin/settings
     * </pre>
     * <p/>
     * then the action name for this mapper has to be set to <code>admin</code> as well.
     *
     * @param mapperName the name of this action mapper. Must not be null.
     * @throws NullPointerException if the mapper name is null
     */
    public AbstractUriPathSegmentActionMapper(String mapperName) {
        this(mapperName, mapperName);
    }

    public AbstractUriPathSegmentActionMapper(String mapperName, String pathSegment) {
        Preconditions.checkNotNull(mapperName);
        this.mapperName = mapperName;
        if (pathSegment == null) {
            pathSegment = mapperName;
        }
        this.pathSegment = pathSegment;
    }

    @Override
    public final String getMapperName() {
        return mapperName;
    }

    protected final String getSegmentInfo() {
        if (mapperName.equals(pathSegment)) {
            return mapperName;
        } else {
            return String.format("%s[%s]", pathSegment, mapperName);
        }
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
     *   application's context path  \___/ URI path interpreted by the URI action mapper tree
     * </pre>
     * <p/>
     * where the URI action mapper for token <code>home</code> is a sub-class of {@link
     * AbstractUriPathSegmentActionMapper}, then this mapper's action command is used as the outcome of the URI
     * interpretation. This command could then provide some logic for the interpreted URI, such as redirecting to the
     * correct home screen for the currently signed in user, or performing some other action.
     *
     * @param command action command to be used when interpreting a URI which points directly to this action mapper. May
     *                be <code>null</code>.
     */
    @Override
    public final void setActionCommandClass(Class<? extends UriActionCommand> command) {
        actionCommand = command;
    }

    @Override
    public final Class<? extends UriActionCommand> getActionCommand() {
        return actionCommand;
    }

    @Override
    public final void registerURIParameter(UriParameter<?> parameter) {
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

    @Override
    public final Class<? extends UriActionCommand> interpretTokens(CapturedParameterValues capturedParameterValues,
                                                                   String currentMapperName,
                                                                   List<String> uriTokens,
                                                                   Map<String, String> queryParameters,
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

    protected abstract Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                             String currentMapperName,
                                                                             List<String> uriTokens,
                                                                             Map<String, String> parameters,
                                                                             ParameterMode parameterMode);

    public void registerSubMapperName(String subMapperName) {
        if (parentMapper != null) {
            parentMapper.registerSubMapperName(subMapperName);
        }
    }

    @Override
    public boolean isResponsibleForToken(String uriToken) {
        return pathSegment.equals(uriToken);
    }

    @Override
    public UriPathSegmentActionMapper getParentMapper() {
        return parentMapper;
    }

    /**
     * Sets the parent action mapper for this object. An action mapper can only be added as sub-mapper to one action
     * mapper. In other words, an action mapper can only have one parent.
     *
     * @param parent the parent mapper for this action mapper
     */
    @Override
    public final void setParentMapper(UriPathSegmentActionMapper parent) {
        parentMapper = parent;
    }

    /**
     * Returns a map of all registered sub-mappers for this URI action mapper. This method is only implemented by {@link
     * DispatchingUriPathSegmentActionMapper} since this is the only URI action mapper implementation in the framework
     * which can have sub-mappers. All other subclasses of {@link AbstractUriPathSegmentActionMapper} return an empty
     * map.
     *
     * @return map containing a mapping of URI tokens on the corresponding sub-mappers that handle these tokens.
     */
    protected Map<String, UriPathSegmentActionMapper> getSubMapperMap() {
        return Collections.emptyMap();
    }

    @Override
    public void assembleUriFragmentTokens(CapturedParameterValues capturedParameterValues, List<String> tokens, ParameterMode parameterMode) {
        tokens.add(getMapperNameInstanceForAssembledUriFragment(capturedParameterValues));
        if (parameterMode != ParameterMode.QUERY) {
            getUriParameters().entrySet().stream().forEach(stringUriParameterEntry -> {
                if (capturedParameterValues.hasValueFor(mapperName, stringUriParameterEntry.getKey())) {
                    final ParameterValue<?> parameterValue = capturedParameterValues.getValueFor(mapperName, stringUriParameterEntry.getKey());
                    stringUriParameterEntry.getValue().toUriTokenList(parameterValue, tokens, parameterMode);
                }
            });
        }
    }

    protected String getMapperNameInstanceForAssembledUriFragment(CapturedParameterValues capturedParameterValues) {
        return mapperName;
    }

    @Override
    public String toString() {
        return String.format("[%s='%s']", getClass().getSimpleName(), mapperName);
    }

    protected String getParameterListAsString() {
        if (getUriParameters().isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(", ");
        getUriParameters().values().stream().forEach(uriParameter -> joiner.add(uriParameter.toString()));
        return "[" + joiner.toString() + "]";
    }

    /**
     * Helper class for interpreting parameter values.
     */
    static class ParameterInterpreter implements Serializable {
        private String mapperName;

        ParameterInterpreter(String mapperName) {
            this.mapperName = mapperName;
        }

        CapturedParameterValues interpretDirectoryParameters(Set<String> registeredUriParameterNames,
                                                             Map<String, UriParameter<?>> registeredUriParameters,
                                                             CapturedParameterValues consumedValues,
                                                             List<String> uriTokens) {
            Map<String, String> directoryBasedParameterMap = new HashMap<>(4);
            for (Iterator<String> it = uriTokens.iterator(); it.hasNext(); ) {
                String parameterName = it.next();
                if (registeredUriParameterNames.contains(parameterName)) {
                    it.remove();

                    if (it.hasNext()) {
                        directoryBasedParameterMap.put(parameterName, it.next());
                        it.remove();
                    }
                } else {
                    break;
                }
            }
            return interpretQueryParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        CapturedParameterValues interpretNamelessDirectoryParameters(Map<String, UriParameter<?>> registeredUriParameters,
                                                                     CapturedParameterValues consumedValues,
                                                                     List<String> uriTokens) {
            Map<String, String> directoryBasedParameterMap = new HashMap<>(4);
            outerLoop:
            for (UriParameter<?> parameter : registeredUriParameters.values()) {
                for (String parameterName : parameter.getParameterNames()) {
                    directoryBasedParameterMap.put(parameterName, uriTokens.remove(0));
                    if (uriTokens.isEmpty()) {
                        break outerLoop;
                    }
                }
            }

            return interpretQueryParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        CapturedParameterValues interpretQueryParameters(Map<String, UriParameter<?>> registeredUriParameters,
                                                         CapturedParameterValues capturedParameterValues,
                                                         Map<String, String> queryParameters) {
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
