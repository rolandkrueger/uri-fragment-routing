package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionMapperTree;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Abstract default implementation for interface {@link UriPathSegmentActionMapper}.
 */
public abstract class AbstractUriPathSegmentActionMapper implements UriPathSegmentActionMapper {
    private static final long serialVersionUID = 8450975393827044559L;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractUriPathSegmentActionMapper.class);

    private Map<String, UriParameter<?>> registeredUriParameters;
    private Set<String> registeredUriParameterNames;
    private UriPathSegmentActionMapper parentMapper;
    private Class<? extends UriActionCommand> actionCommand;
    private final String mapperName;
    private final String pathSegment;

    /**
     * Creates a new action mapper for the given mapper name. The mapper name must not be {@code null}. This name is
     * directly used to specify the path segment of a URI fragment for which this action mapper is responsible. For
     * example, if the mapper name is <tt>admin</tt> then this mapper is responsible for the <tt>admin</tt> part in the
     * following URI
     * <pre>
     * http://www.example.com/app#!admin/settings
     * </pre>
     * Since a mapper name has to be unique in an instance of a {@link org.roklib.urifragmentrouting.UriActionMapperTree},
     * you can only use one action mapper created with this constructor per action mapper tree. This means in turn that
     * the path segment name implicitly specified by this constructor can also appear only once in an action mapper
     * tree. If you want to reuse the same path segment name for more than one action mapper, use the two argument
     * constructor {@link #AbstractUriPathSegmentActionMapper(String, String)}.
     *
     * @param mapperName the name of this action mapper. Must not be {@code null}.
     * @throws NullPointerException if the mapper name is {@code null}
     */
    public AbstractUriPathSegmentActionMapper(final String mapperName) {
        this(mapperName, mapperName);
    }

    /**
     * Creates a new action mapper for the given mapper name and path segment name. The mapper name is used to uniquely
     * identify this action mapper instance in an {@link org.roklib.urifragmentrouting.UriActionMapperTree}. The path
     * segment name is that part of a URI fragment for which this action mapper is responsible.
     *
     * @param mapperName  the name of this action mapper. Must not be {@code null}.
     * @param pathSegment the path segment name for which this action mapper is responsible. If this is {@code null},
     *                    the mapper name will be used to define the path segment name instead.
     * @throws NullPointerException if the mapper name is {@code null}
     */
    public AbstractUriPathSegmentActionMapper(final String mapperName, String pathSegment) {
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

    /**
     * Returns an informational String about the path segment name of this action mapper. This is either the path
     * segment name itself or the mapper name added to the path segment name if these two differ. This method is used
     * for creating and logging an overview of the current {@link org.roklib.urifragmentrouting.UriActionMapperTree}
     * with {@link UriActionMapperTree#getMapperOverview()}.
     *
     * @return an informational String about the path segment name of this action mapper for logging purposes
     */
    protected String getSegmentInfo() {
        if (mapperName.equals(pathSegment)) {
            return mapperName;
        } else {
            return String.format("%s[%s]", pathSegment, mapperName);
        }
    }

    @Override
    public final void setActionCommandClass(final Class<? extends UriActionCommand> command) {
        actionCommand = command;
    }

    @Override
    public final Class<? extends UriActionCommand> getActionCommand() {
        return actionCommand;
    }

    @Override
    public final void registerURIParameter(final UriParameter<?> parameter) {
        Preconditions.checkNotNull(parameter);

        if (registeredUriParameters == null) {
            registeredUriParameters = new LinkedHashMap<>();
            registeredUriParameterNames = new HashSet<>();
        }

        if (registeredUriParameters.containsKey(parameter.getId())) {
            throw new IllegalArgumentException("Another parameter with the same id is already registered on this mapper.");
        }

        parameter.getParameterNames().forEach(parameterName -> {
            if (registeredUriParameterNames.contains(parameterName)) {
                throw new IllegalArgumentException("Cannot register parameter " + parameter +
                        ". Another parameter with parameter name '" + parameterName +
                        "' is already registered on this mapper.");
            }
            registeredUriParameters.put(parameter.getId(), parameter);
            registeredUriParameterNames.add(parameterName);
        });
    }

    /**
     * Returns the set of URI parameters which have been registered with this action mapper. If no parameters have been
     * registered an empty map is returned.
     *
     * @return the set of URI parameters which have been registered with this action mapper.
     */
    protected final Map<String, UriParameter<?>> getUriParameters() {
        return registeredUriParameters == null ? Collections.emptyMap() : registeredUriParameters;
    }

    /**
     * Returns the combined set of all parameter names from all URI parameters registered with this action mapper. This
     * list may be as large as or larger (but never smaller) than the map returned by {@link #getUriParameters()}. This
     * is due to the fact that a single {@link UriParameter} may consist of more than one value (e. g. {@link
     * org.roklib.urifragmentrouting.parameter.Point2DUriParameter}.
     * <p>
     * If no parameters have been registered an empty map is returned.
     *
     * @return the combined set of all parameter names from all URI parameters registered with this action mapper.
     */
    protected final Set<String> getUriParameterNames() {
        return registeredUriParameterNames == null ? Collections.emptySet() : registeredUriParameterNames;
    }

    @Override
    public final Class<? extends UriActionCommand> interpretTokens(final CapturedParameterValues capturedParameterValues,
                                                                   final String currentUriToken,
                                                                   final List<String> uriTokens,
                                                                   final Map<String, String> queryParameters,
                                                                   final ParameterMode parameterMode) {
        LOG.debug("interpretTokens() - Current token: {}, remaining tokens: {}{}",
                currentUriToken == null ? "<UNDEFINED>" : currentUriToken,
                uriTokens, queryParameters.isEmpty() ? "" : ", query parameters: " + queryParameters);

        if (!getUriParameters().isEmpty()) {
            final ParameterInterpreter interpreter = new ParameterInterpreter(mapperName);
            if (parameterMode == ParameterMode.QUERY) {
                interpreter.interpretParameters(getUriParameters(), capturedParameterValues, queryParameters);
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

        return interpretTokensImpl(capturedParameterValues, currentUriToken, uriTokens, queryParameters, parameterMode);
    }

    /**
     * Interprets the given list of URI fragment tokens to find an action command class which is to be executed for the
     * currently interpreted URI fragment. This method has the same semantics as {@link
     * #interpretTokens(CapturedParameterValues, String, List, Map, ParameterMode)} except that all URI parameters
     * registered with this action mapper have already been extracted from the {@code uriTokens} and {@code
     * queryParameters} by {@link AbstractUriPathSegmentActionMapper}.
     *
     * @param capturedParameterValues the current set of parameter values which have already been converted from their
     *                                String representations as salvaged from the current set of URI tokens. For all URI
     *                                parameters registered on this action mapper, this method tries to find parameter
     *                                values from the current set of {@code uriTokens} and {@code queryParameters}. Such
     *                                values are converted and added to the {@code capturedParameterValues}.
     * @param currentUriToken         the URI token which is currently being interpreted by this action mapper
     * @param uriTokens               the list of URI tokens which still have to be interpreted. Tokens which have
     *                                already been interpreted, either because they identify the current action mapper
     *                                or because they belong to one of the URI parameters registered with this action
     *                                mapper, have to be removed from this list, so that this list will be empty at the
     *                                end of the interpretation process
     * @param queryParameters         map of parameter values which were appended to the currently interpreted URI
     *                                fragment in Query Parameter Mode. May be empty.
     * @param parameterMode           the {@link ParameterMode} to be used when capturing the URI parameters from the
     *                                URI token list and query parameter map
     * @return the readily configured URI action command class from this action mapper or from one of this mapper's
     * sub-mappers. If no such command class could be found, {@code null} is returned.
     */
    protected abstract Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                             String currentUriToken,
                                                                             List<String> uriTokens,
                                                                             Map<String, String> queryParameters,
                                                                             ParameterMode parameterMode);

    @Override
    public void registerSubMapperName(final String subMapperName) {
        if (parentMapper != null) {
            parentMapper.registerSubMapperName(subMapperName);
        }
    }

    @Override
    public boolean isResponsibleForToken(final String uriToken) {
        return pathSegment.equals(uriToken);
    }

    @Override
    public UriPathSegmentActionMapper getParentMapper() {
        return parentMapper;
    }

    @Override
    public final void setParentMapper(final UriPathSegmentActionMapper parent) {
        parentMapper = parent;
    }

    /**
     * Returns a map of all registered sub-mappers for this URI action mapper. This method is only overridden by {@link
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
    public void assembleUriFragmentTokens(final CapturedParameterValues parameterValues, final List<String> uriTokens, final ParameterMode parameterMode) {
        uriTokens.add(getPathSegmentNameForAssemblingUriFragment(parameterValues));
        if (parameterMode != ParameterMode.QUERY) {
            getUriParameters().entrySet().forEach(stringUriParameterEntry -> {
                if (parameterValues.hasValueFor(mapperName, stringUriParameterEntry.getKey())) {
                    final ParameterValue<?> parameterValue = parameterValues.getValueFor(mapperName, stringUriParameterEntry.getKey());
                    stringUriParameterEntry.getValue().toUriTokenList(parameterValue, uriTokens, parameterMode);
                }
            });
        }
    }

    /**
     * Provides the path segment name for this action mapper. By default, this is the value provided through the
     * constructor {@link #AbstractUriPathSegmentActionMapper(String, String)} (or implicitly the mapper name when
     * constructor {@link AbstractUriPathSegmentActionMapper(String)} is used). Subclasses may override this method when
     * their path segment name needs to be calculated from the set of {@link ParameterValue}s given with the {@code
     * capturedParameterValues}. This is done, for instance, by the {@link RegexUriPathSegmentActionMapper}.
     *
     * @param capturedParameterValues the set of {@link ParameterValue}s to be used to parameterize the generated URI
     *                                fragment
     * @return the path segment name for this action mapper to be used to assemble a parameterized URI fragment for a
     */
    protected String getPathSegmentNameForAssemblingUriFragment(final CapturedParameterValues capturedParameterValues) {
        return pathSegment;
    }

    @Override
    public String toString() {
        return String.format("[%s name='%s', segment='%s']", getClass().getSimpleName(), mapperName, pathSegment);
    }

    /**
     * Generates an informative String which includes information about all {@link UriParameter}s registered with this
     * action mapper. This is used for logging purposes.
     *
     * @return an informative String about all {@link UriParameter}s registered with this action mapper.
     */
    protected String getParameterListAsString() {
        if (getUriParameters().isEmpty()) {
            return "";
        }
        final StringJoiner joiner = new StringJoiner(", ");
        getUriParameters().values().forEach(uriParameter -> joiner.add(uriParameter.toString()));
        return "[" + joiner.toString() + "]";
    }

    /**
     * Helper class for interpreting parameter values.
     */
    static class ParameterInterpreter implements Serializable {
        private final String mapperName;

        ParameterInterpreter(final String mapperName) {
            this.mapperName = mapperName;
        }

        CapturedParameterValues interpretDirectoryParameters(final Set<String> registeredUriParameterNames,
                                                             final Map<String, UriParameter<?>> registeredUriParameters,
                                                             final CapturedParameterValues consumedValues,
                                                             final List<String> uriTokens) {
            final Map<String, String> directoryBasedParameterMap = new HashMap<>(4);
            for (final Iterator<String> it = uriTokens.iterator(); it.hasNext(); ) {
                final String parameterName = it.next();
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
            return interpretParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        CapturedParameterValues interpretNamelessDirectoryParameters(final Map<String, UriParameter<?>> registeredUriParameters,
                                                                     final CapturedParameterValues consumedValues,
                                                                     final List<String> uriTokens) {
            final Map<String, String> directoryBasedParameterMap = new HashMap<>(4);
            outerLoop:
            for (final UriParameter<?> parameter : registeredUriParameters.values()) {
                for (final String parameterName : parameter.getParameterNames()) {
                    directoryBasedParameterMap.put(parameterName, uriTokens.remove(0));
                    if (uriTokens.isEmpty()) {
                        break outerLoop;
                    }
                }
            }

            return interpretParameters(registeredUriParameters, consumedValues, directoryBasedParameterMap);
        }

        CapturedParameterValues interpretParameters(final Map<String, UriParameter<?>> registeredUriParameters,
                                                    final CapturedParameterValues capturedParameterValues,
                                                    final Map<String, String> queryParameters) {
            LOG.debug("interpretParameters() - Trying to extract values for registered parameters {} from current parameter set {}", registeredUriParameters, queryParameters);
            registeredUriParameters
                    .values().forEach(parameter -> {
                final ParameterValue<?> consumedParameterValue = parameter.consumeParameters(queryParameters);
                if (consumedParameterValue != null) {
                    parameter.getParameterNames().forEach(queryParameters::remove);
                }
                if (consumedParameterValue.hasValue()) {
                    LOG.debug("interpretParameters() - Found value for parameter {}: {}", parameter, consumedParameterValue);
                } else {
                    LOG.debug("interpretParameters() - No value found for parameter {}", parameter);
                }
                capturedParameterValues.setValueFor(mapperName, parameter, consumedParameterValue);
            });

            return capturedParameterValues;
        }
    }
}
