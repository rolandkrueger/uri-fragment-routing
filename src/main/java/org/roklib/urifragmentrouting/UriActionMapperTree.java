package org.roklib.urifragmentrouting;

import org.roklib.urifragmentrouting.helper.ActionCommandFactory;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.DispatchingUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.SimpleUriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.AbstractSingleUriParameter;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.SingleValuedParameterFactory;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.strategy.DirectoryStyleUriTokenExtractionStrategyImpl;
import org.roklib.urifragmentrouting.strategy.QueryParameterExtractionStrategy;
import org.roklib.urifragmentrouting.strategy.StandardQueryNotationQueryParameterExtractionStrategyImpl;
import org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class is the central entry point into the URI fragment routing framework. It represents and manages the complete
 * set of URI fragments which are available for a web application. This class is chiefly responsible for the following
 * two tasks: <ul> <li>It interprets a URI fragment visited by a user, extracts parameter values from this URI fragment,
 * and resolves it into a URI action command (see {@link UriActionCommand}) which is eventually executed.</li> <li>It
 * creates fully parameterized URI fragments for any of the {@link UriPathSegmentActionMapper}s contained in this tree
 * which can be used for defining link targets in a web application.</li> </ul>
 * <p>
 * When a URI fragment is to be interpreted by this URI action mapper tree, that fragment has to be passed to method
 * {@link #interpretFragment(String)} or {@link #interpretFragment(String, Object)} (the latter method is used when an
 * application-specific context object is to be passed along to the URI action command to be found for the URI
 * fragment). The URI fragment is then split into a token list to be recursively interpreted by the action mappers
 * registered on this action mapper tree. Each action mapper is responsible for handling one of the URI tokens which
 * represent the individual path segments of the currently interpreted URI fragment.The strategy for splitting a URI
 * fragment into a URI token list is defined by the {@link UriTokenExtractionStrategy} set for this mapper tree. For
 * example, if the following URI is to be interpreted
 * <pre>
 * http://www.example.com/myapp#!/user/home/messages
 * </pre>
 * for a web application running under context <code>http://www.example.com/myapp/</code>, the URI fragment to be
 * interpreted by the action mapper tree is <code>/user/home/messages</code>. This is split into three individual URI
 * tokens <code>user</code>, <code>home</code>, and <code>messages</code>, in that order. Each URI token stands for one
 * individual path segment of the URI fragment. To interpret these tokens, the URI action mapper tree passes this token
 * list to the sub-mapper which is responsible for handling the first path segment <code>user</code>. This action mapper
 * in turn passes the remaining tokens to one of its own sub-mapper which is responsible for handling the
 * <code>home</code> path segment. The URI token list is thus interpreted recursively by the sub-mappers of the mapper
 * tree until eventually the mapper responsible for the final URI token is reached. This action mapper will return its
 * action command class which will then be instantiated and executed as the result of interpreting the full URI
 * fragment.
 * <p>
 * If no final mapper could be found for the remaining URI tokens in the list, either nothing is done or the default
 * action command is executed. <h1>Default action command</h1> A default action command class can be specified with
 * {@link #setDefaultActionCommandClass(Class)}. This default action command thus indicates that the current URI fragment could not
 * successfully be interpreted. It will be executed when the interpretation process of some URI fragment does not yield
 * any action class. Such a default action command could be used to show a Page Not Found error page to the user, for
 * example.
 * <p>
 * <h1>URI parameters</h1> Besides specifying a path structure of URI fragments which point to individual action command
 * classes, parameter values can be added to each path segment of a URI fragment. By that, it is possible to
 * parameterize the action command objects which are executed as the last step of interpreting a URI fragment. For
 * example, to parameterize the URI fragment shown above, one could add a user ID to the <code>user</code> path
 * segment:
 * <pre>
 *     /user/id/42/home/messages
 * </pre>
 * The two path segments <code>id</code> and <code>42</code> together form a URI parameter value. URI parameters can be
 * registered on {@link UriPathSegmentActionMapper} instances with method {@link UriPathSegmentActionMapper#registerURIParameter(UriParameter)}.
 * In the example, the action mapper responsible for the <code>user</code> path segment has one registered parameter
 * <code>id</code>. During the interpretation process of a URI fragment, all parameter values found in the fragment are
 * extracted and converted into their respective data type using a {@link org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter}.
 * URI parameters are represented by a class implementing the {@link UriParameter} interface. A URI parameter does
 * always belong to a {@link UriPathSegmentActionMapper}, i. e. it is possible to register the same parameter on more
 * than one action mapper. <h1>Parameter mode</h1> A {@link ParameterMode} can be set for a {@link UriActionMapperTree}
 * with {@link #setParameterMode(ParameterMode)}. This mode defines the way how a URI parameter is contained in a URI
 * fragment. There are three different modes available. The following list shows the example from above using the three
 * different parameter modes: <ul> <li>{@link ParameterMode#DIRECTORY_WITH_NAMES}:
 * <code>/user/id/42/home/messages</code></li> <li>{@link ParameterMode#DIRECTORY}:
 * <code>/user/42/home/messages</code></li> <li>{@link ParameterMode#QUERY}: <code>/user/home/messages?id=42</code></li>
 * </ul> (Note that the current {@link UriTokenExtractionStrategy} and {@link QueryParameterExtractionStrategy}
 * determine the syntax of the URI fragment path structure and query parameters. See below for details.)
 * <p>
 * By default, if not specified differently, the {@link ParameterMode} used by the {@link UriActionMapperTree} is {@link
 * ParameterMode#DIRECTORY_WITH_NAMES}. <h1>URI token and query parameter extraction strategy</h1> There are two
 * strategy interfaces which define how a URI fragment is disassembled into its constituents. Interface {@link
 * UriTokenExtractionStrategy} specifies the algorithm that splits a URI fragment into a list of URI tokens. Interface
 * {@link QueryParameterExtractionStrategy} specifies the algorithm that extracts URI parameters from the URI fragment
 * in query mode. By default, the {@link UriActionMapperTree} uses the two standard implementations of these interfaces.
 * These are {@link DirectoryStyleUriTokenExtractionStrategyImpl} and {@link StandardQueryNotationQueryParameterExtractionStrategyImpl},
 * respectively. <h1>Routing context</h1> When a URI fragment is interpreted by the {@link UriActionMapperTree}, a
 * custom routing context object can be passed along that interpretation process. This context object can be passed to
 * the URI action command which will be executed if this context is required by the action command (see annotation
 * {@link org.roklib.urifragmentrouting.annotation.RoutingContext}). Using this routing context object, an application
 * can pass application- and user-specific data to the URI action command. For example, a reference to the current user
 * session could be passed along with the routing context.
 * <p>
 * The routing context object can be specified with {@link #interpretFragment(String, Object)}. <h1>Thread safety</h1>
 * The URI fragment routing framework is thread-safe. This means that you typically have one application-scoped instance
 * of a {@link UriActionMapperTree} which contains all available URI fragments handled by a single application. In other
 * words, it is not necessary to store an instance of {@link UriActionMapperTree} in the user session. <h1>Constructing
 * a URI action mapper tree with a builder</h1>There are two options to construct a {@link UriActionMapperTree}: First,
 * you can instantiate all action mapper objects yourself, stick them together and add all root action mappers to a
 * {@link UriActionMapperTree} with <code>getRootActionMapper().addSubMapper(UriPathSegmentActionMapper)</code>. The
 * second option is to use the {@link UriActionMapperTree.UriActionMapperTreeBuilder} to build a URI action mapper tree
 * with a fluent API. To start building a URI action mapper tree, you start with the following code:
 * <pre>
 * MapperTreeBuilder builder = UriActionMapperTree.create().buildMapperTree();
 * </pre>
 * Use the {@link MapperTreeBuilder} to construct the complete URI action mapper tree. Building a mapper tree is a
 * depth-first approach. That is, you first create one of the root mappers, then add one of its sub-mappers, then in
 * turn add one of the sub-mappers's sub-mapper and so forth until you reach a leaf action mapper. By calling one of the
 * builders' <code>finishMapper()</code> methods, you can move one step up the mapping tree and start building the
 * siblings of the mapper you just finished.
 */
public class UriActionMapperTree {

    private static final Logger LOG = LoggerFactory.getLogger(UriActionMapperTree.class);

    private ParameterMode parameterMode = ParameterMode.DIRECTORY_WITH_NAMES;
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;
    private Class<? extends UriActionCommand> defaultActionCommandClass;

    /**
     * Base dispatching mapper that contains all root action mappers.
     */
    private final DispatchingUriPathSegmentActionMapper rootMapper;
    /**
     * Set comprising the mapper names of all action mappers contained in this URI action mapper tree.
     */
    private final Set<String> mapperNamesInUse;

    private UriActionMapperTree() {
        queryParameterExtractionStrategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
        uriTokenExtractionStrategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
        rootMapper = new DispatchingUriPathSegmentActionMapper("") {
            @Override
            public String toString() {
                return "[Root Dispatching Action Mapper]";
            }
        };
        rootMapper.setParentMapper(new AbstractUriPathSegmentActionMapper("") {
            private static final long serialVersionUID = 3744506992900879054L;

            protected Class<? extends UriActionCommand> interpretTokensImpl(final CapturedParameterValues capturedParameterValues,
                                                                            final String currentUriToken,
                                                                            final List<String> uriTokens,
                                                                            final Map<String, String> queryParameters,
                                                                            final ParameterMode parameterMode) {
                return null;
            }

            @Override
            public boolean isResponsibleForToken(final String uriToken) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void getMapperOverview(final String path, final List<String> mapperOverviewList) {
            }

            @Override
            public void registerSubMapperName(final String subMapperName) {
                if (isMapperNameInUse(subMapperName)) {
                    throw new IllegalArgumentException("Mapper name '" + subMapperName + "' is already in use");
                }
                addUsedMapperName(subMapperName);
            }
        });
        mapperNamesInUse = new HashSet<>();
    }

    public UriActionCommand interpretFragment(final String uriFragment) {
        return interpretFragment(uriFragment, null);
    }

    /**
     * This method is the central entry point for the URI action handling framework.
     *
     * @param uriFragment relative URI to be interpreted by the URI action handling framework. This may be an URI such
     *                    as <code>/admin/configuration/settings?language=de</code>
     * @return the command responsible for the given <code>uriFragment</code> or <code>null</code> if the fragment could
     * not be resolved to any command
     */
    public <C> UriActionCommand interpretFragment(final String uriFragment, final C context) {
        final UUID uuid = UUID.randomUUID();
        LOG.info("[{}] interpretFragment() - INTERPRET - [ {} ]", uuid, uriFragment);
        LOG.debug("[{}] interpreting fragment [ {} ] - PARAMETER_MODE={} - CONTEXT={}", uuid, uriFragment, parameterMode, context);
        final CapturedParameterValues capturedParameterValues = new CapturedParameterValues();
        final Class<? extends UriActionCommand> actionCommandClass = getActionForUriFragment(capturedParameterValues,
                uriFragment,
                uriTokenExtractionStrategy.extractUriTokens(queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(uriFragment)),
                queryParameterExtractionStrategy.extractQueryParameters(uriFragment),
                parameterMode,
                uuid);

        if (actionCommandClass != null) {
            final UriActionCommand actionCommandObject = createAndConfigureUriActionCommand(uriFragment, context, capturedParameterValues, actionCommandClass);
            LOG.debug("[{}] interpretFragment() - Running action command object {}", uuid, actionCommandObject);
            actionCommandObject.run();
            return actionCommandObject;
        }
        LOG.debug("[{}] interpretFragment() - No action command class found for fragment", uuid);
        return null;
    }

    private <C> UriActionCommand createAndConfigureUriActionCommand(final String uriFragment, final C context, final CapturedParameterValues capturedParameterValues, final Class<? extends UriActionCommand> actionCommandClass) {
        final ActionCommandFactory<C> factory = new ActionCommandFactory<>(actionCommandClass);
        final UriActionCommand actionCommandObject = factory.createCommand();
        factory.passRoutingContext(context, actionCommandClass, actionCommandObject);
        factory.passUriFragment(uriFragment, actionCommandClass, actionCommandObject);
        factory.passAllCapturedParameters(capturedParameterValues, actionCommandClass, actionCommandObject);
        factory.passCapturedParameters(capturedParameterValues, actionCommandClass, actionCommandObject);
        return actionCommandObject;
    }

    /**
     * Set the parameter mode to be used for interpreting the visited URIs.
     *
     * @param parameterMode {@link ParameterMode} which will be used by {@link #interpretFragment(String, Object)}
     */
    private void setParameterMode(final ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

    private void setQueryParameterExtractionStrategy(final QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
        Preconditions.checkNotNull(queryParameterExtractionStrategy);
        this.queryParameterExtractionStrategy = queryParameterExtractionStrategy;
    }

    private void setUriTokenExtractionStrategy(final UriTokenExtractionStrategy uriTokenExtractionStrategy) {
        Preconditions.checkNotNull(uriTokenExtractionStrategy);
        this.uriTokenExtractionStrategy = uriTokenExtractionStrategy;
    }

    public static UriActionMapperTreeBuilder create() {
        return new UriActionMapperTreeBuilder();
    }

    /**
     * Needed by unit tests.
     */
    Collection<UriPathSegmentActionMapper> getRootActionMappers() {
        return getRootActionMapper().getSubMapperMap().values();
    }

    public String assembleUriFragment(final UriPathSegmentActionMapper forMapper) {
        return assembleUriFragment(new CapturedParameterValues(), forMapper);
    }

    public String assembleUriFragment(final CapturedParameterValues capturedParameterValues, final UriPathSegmentActionMapper forMapper) {
        Preconditions.checkNotNull(forMapper);
        final Stack<UriPathSegmentActionMapper> mapperStack = buildMapperStack(forMapper);

        final List<String> uriTokens = new LinkedList<>();
        while (!mapperStack.isEmpty()) {
            final UriPathSegmentActionMapper mapper = mapperStack.pop();
            mapper.assembleUriFragmentTokens(capturedParameterValues, uriTokens, parameterMode);
        }

        String queryParamSection = "";
        if (parameterMode == ParameterMode.QUERY) {
            queryParamSection = queryParameterExtractionStrategy.assembleQueryParameterSectionForUriFragment(capturedParameterValues.asQueryParameterMap());
        }

        return uriTokenExtractionStrategy.assembleUriFragmentFromTokens(uriTokens) +
                queryParamSection;
    }

    private Stack<UriPathSegmentActionMapper> buildMapperStack(final UriPathSegmentActionMapper forMapper) {
        final Stack<UriPathSegmentActionMapper> stack = new Stack<>();

        UriPathSegmentActionMapper currentMapper = forMapper;
        do {
            stack.push(currentMapper);
            currentMapper = currentMapper.getParentMapper();
            if (currentMapper == null) {
                throw new IllegalArgumentException("given mapper instance is not part of the mapper tree");
            }
        } while (currentMapper != getRootActionMapper());

        return stack;
    }

    /**
     * Returns the {@link DispatchingUriPathSegmentActionMapper} which is the root action mapper of this URI action
     * mapper tree. This is a special action mapper since the path segment name it is responsible for is the empty
     * String. Thus, when the current URI fragment is to be interpreted by this URI action mapper tree, this URI
     * fragment is first passed to that root dispatching mapper. All URI action mappers which are responsible for the
     * first directory level of a URI fragment will have to be added to this root mapper as sub-mappers.
     *
     * @return the root dispatching mapper for this URI action mapper tree
     */
    public DispatchingUriPathSegmentActionMapper getRootActionMapper() {
        return rootMapper;
    }

    /**
     * Sets the default action command to be executed each time when no responsible action mapper could be found for
     * some URI fragment. If set to {@code null} no particular action is performed when no URI action command could be
     * found for the currently interpreted URI fragment.
     *
     * @param defaultActionCommandClass default command to be executed when no URI action command could be found for the currently
     *                                  interpreted URI fragment. May be {@code null}.
     */
    public void setDefaultActionCommandClass(final Class<? extends UriActionCommand> defaultActionCommandClass) {
        this.defaultActionCommandClass = defaultActionCommandClass;
    }

    private Class<? extends UriActionCommand> getActionForUriFragment(final CapturedParameterValues capturedParameterValues,
                                                                      final String uriFragment,
                                                                      final List<String> uriTokens,
                                                                      final Map<String, String> extractedQueryParameters,
                                                                      final ParameterMode parameterMode,
                                                                      final UUID uuid) {

        final Class<? extends UriActionCommand> action = rootMapper.interpretTokens(capturedParameterValues, null, uriTokens, extractedQueryParameters, parameterMode);

        if (action == null) {
            LOG.info("[{}] getActionForUriFragment() - NOT_FOUND - No registered URI action mapper found for fragment: {}", uuid, uriFragment);
            if (defaultActionCommandClass != null) {
                LOG.info("[{}] getActionForUriFragment() - NOT_FOUND - Executing default action command class: {}", uuid, defaultActionCommandClass);
            }
            return defaultActionCommandClass;
        }
        return action;
    }

    private boolean isMapperNameInUse(final String mapperName) {
        return mapperNamesInUse.contains(mapperName);
    }

    private void addUsedMapperName(final String mapperName) {
        mapperNamesInUse.add(mapperName);
    }

    public List<String> getMapperOverview() {
        final List<String> result = new LinkedList<>();
        getRootActionMapper()
                .getSubMapperMap()
                .values()
                .forEach(mapper -> mapper.getMapperOverview("", result));
        return result;
    }

    /**
     * Print the mapper overview created with {@link #getMapperOverview()} to the given {@link PrintStream}.
     *
     * @param target target stream where to print out the action mapper overview
     */
    public void print(final PrintStream target) {
        getMapperOverview().forEach(target::println);
    }

    public static class UriActionMapperTreeBuilder {
        final UriActionMapperTree uriActionMapperTree;

        private UriActionMapperTreeBuilder() {
            uriActionMapperTree = new UriActionMapperTree();
        }

        /**
         * Start building a URI action mapper tree.
         *
         * @return the root {@link MapperTreeBuilder}
         */
        public MapperTreeBuilder buildMapperTree() {
            LOG.info("buildMapperTree() - Starting to build a mapper tree.");
            return new MapperTreeBuilder(uriActionMapperTree, uriActionMapperTree.getRootActionMapper());
        }

        /**
         * Specify the {@link UriTokenExtractionStrategy} the constructed URI action mapper tree shall use.
         *
         * @param uriTokenExtractionStrategy the concrete {@link UriTokenExtractionStrategy} to be used
         * @return a builder object
         * @see #setUriTokenExtractionStrategy(UriTokenExtractionStrategy)
         */
        public UriActionMapperTreeBuilder useUriTokenExtractionStrategy(final UriTokenExtractionStrategy uriTokenExtractionStrategy) {
            uriActionMapperTree.setUriTokenExtractionStrategy(uriTokenExtractionStrategy);
            return this;
        }

        /**
         * Specify the {@link QueryParameterExtractionStrategy} the constructed URI action mapper tree shall use.
         *
         * @param queryParameterExtractionStrategy the concrete {@link QueryParameterExtractionStrategy} to be used
         * @return a builder object
         * @see #setQueryParameterExtractionStrategy(QueryParameterExtractionStrategy)
         */
        public UriActionMapperTreeBuilder useQueryParameterExtractionStrategy(final QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
            uriActionMapperTree.setQueryParameterExtractionStrategy(queryParameterExtractionStrategy);
            return this;
        }

        /**
         * Specify the {@link ParameterMode} to be employed by the constructed URI action mapper tree.
         *
         * @param parameterMode the {@link ParameterMode} to be used
         * @return a builder object
         * @see #setParameterMode(ParameterMode)
         */
        public UriActionMapperTreeBuilder useParameterMode(final ParameterMode parameterMode) {
            uriActionMapperTree.setParameterMode(parameterMode);
            return this;
        }

        /**
         * Specify the default {@link UriActionCommand} class to be used by the constructed URI action mapper tree.
         *
         * @param defaultActionCommandClass the default {@link UriActionCommand} class
         * @return a builder object
         * @see #setDefaultActionCommandClass(Class)
         */
        public UriActionMapperTreeBuilder useDefaultActionCommand(final Class<? extends UriActionCommand> defaultActionCommandClass) {
            uriActionMapperTree.setDefaultActionCommandClass(defaultActionCommandClass);
            return this;
        }
    }

    public static class MapperTreeBuilder {
        private final UriActionMapperTree uriActionMapperTree;
        private final DispatchingUriPathSegmentActionMapper currentDispatchingMapper;
        private MapperTreeBuilder parentBuilder;

        private MapperTreeBuilder(final UriActionMapperTree uriActionMapperTree,
                                  final DispatchingUriPathSegmentActionMapper currentDispatchingMapper,
                                  final MapperTreeBuilder parentBuilder) {
            this(uriActionMapperTree, currentDispatchingMapper);
            this.parentBuilder = parentBuilder;
        }

        private MapperTreeBuilder(final UriActionMapperTree uriActionMapperTree, final DispatchingUriPathSegmentActionMapper currentDispatchingMapper) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.currentDispatchingMapper = currentDispatchingMapper;
        }

        /**
         * Finalize and build the URI action mapper tree.
         *
         * @return the fully constructed {@link UriActionMapperTree} ready to be used
         */
        public UriActionMapperTree build() {
            return uriActionMapperTree;
        }

        /**
         * Finishes the construction of the currently built URI action mapper. After calling this method, a sibling
         * action mapper can be constructed for the action mapper which has just been completed. For each action mapper
         * which is constructed starting with the {@link #map(String)} or {@link #mapSubtree(String)} method (or any of
         * the overloaded variants), This method has to be called exactly once to finalize the construction
         * of this mapper.
         *
         * @return a builder object
         */
        public MapperTreeBuilder finishMapper() {
            return parentBuilder == null ? this : parentBuilder;
        }

        /**
         * Adds a pre-built URI action mapper as sub-mapper to the currently built dispatching mapper.
         *
         * @param mapper the action mapper to be added
         * @return a builder object
         */
        public MapperTreeBuilder addMapper(final UriPathSegmentActionMapper mapper) {
            currentDispatchingMapper.addSubMapper(mapper);
            return this;
        }

        /**
         * Start building a {@link SimpleUriPathSegmentActionMapper} for the given mapper name.
         *
         * @param mapperName the mapper name for which the action mapper is responsible
         * @return a builder object
         */
        public MapperBuilder map(final String mapperName) {
            return new MapperBuilder(this, currentDispatchingMapper, mapperName);
        }

        public SubtreeMapperBuilder mapSubtree(final String mapperName, final String segmentName, final Consumer<DispatchingUriPathSegmentActionMapper> consumer) {
            final SubtreeMapperBuilder subtreeBuilder = mapSubtree(mapperName, segmentName);
            consumer.accept(subtreeBuilder.dispatchingMapper);
            return subtreeBuilder;
        }

        public SubtreeMapperBuilder mapSubtree(final String mapperName, final Consumer<DispatchingUriPathSegmentActionMapper> consumer) {
            return mapSubtree(mapperName, mapperName, consumer);
        }

        public SubtreeMapperBuilder mapSubtree(final String mapperName, final String segmentName) {
            LOG.info("mapSubtree() - Building a subtree for mapper name '{}', segment name '{}' on tree {}", mapperName, segmentName, currentDispatchingMapper);
            final DispatchingUriPathSegmentActionMapper dispatchingMapper = new DispatchingUriPathSegmentActionMapper(mapperName, segmentName);
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }

        public SubtreeMapperBuilder mapSubtree(final String mapperName) {
            return mapSubtree(mapperName, mapperName);
        }

        public SubtreeMapperBuilder mapSubtree(final DispatchingUriPathSegmentActionMapper dispatchingMapper) {
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }
    }

    public static class MapperBuilder {
        private final MapperTreeBuilder parentMapperTreeBuilder;
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private final String mapperName;
        private String pathSegment;

        private MapperBuilder(final MapperTreeBuilder parentMapperTreeBuilder,
                              final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                              final String mapperName) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.mapperName = mapperName;
        }

        public SimpleMapperParameterBuilder onAction(final Class<? extends UriActionCommand> actionCommandClass) {
            Preconditions.checkNotNull(actionCommandClass);
            final SimpleUriPathSegmentActionMapper mapper = new SimpleUriPathSegmentActionMapper(mapperName, pathSegment, actionCommandClass);
            return new SimpleMapperParameterBuilder(parentMapperTreeBuilder, dispatchingMapper, mapper);
        }

        public MapperBuilder onPathSegment(final String pathSegment) {
            this.pathSegment = pathSegment;
            return this;
        }
    }

    public static class SimpleMapperParameterBuilder {
        private final MapperTreeBuilder parentMapperTreeBuilder;
        private final SimpleUriPathSegmentActionMapper targetMapper;
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;

        private SimpleMapperParameterBuilder(final MapperTreeBuilder parentMapperTreeBuilder,
                                             final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                             final SimpleUriPathSegmentActionMapper targetMapper) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.targetMapper = targetMapper;
        }

        public SingleValuedParameterBuilder<SimpleMapperParameterBuilder> withSingleValuedParameter(final String id) {
            return new SingleValuedParameterBuilder<>(this, id, targetMapper);
        }

        public SimpleMapperParameterBuilder withParameter(final UriParameter<?> parameter) {
            targetMapper.registerURIParameter(parameter);
            return this;
        }

        public MapperTreeBuilder finishMapper() {
            dispatchingMapper.addSubMapper(targetMapper);
            return parentMapperTreeBuilder;
        }

        public MapperTreeBuilder finishMapper(final Consumer<SimpleUriPathSegmentActionMapper> consumer) {
            consumer.accept(targetMapper);
            return finishMapper();
        }
    }

    public static class SingleValuedParameterBuilder<B> {
        private final B parentBuilder;
        private final String id;
        private final UriPathSegmentActionMapper targetMapper;

        private SingleValuedParameterBuilder(final B parentBuilder, final String id,
                                             final UriPathSegmentActionMapper targetMapper) {
            this.parentBuilder = parentBuilder;
            this.id = id;
            this.targetMapper = targetMapper;
        }

        public <T> SingleValueParameterWithDefaultValueBuilder<T, B> forType(final Class<T> forType) {
            return new SingleValueParameterWithDefaultValueBuilder<>(parentBuilder, id, forType, targetMapper);
        }

        public static class SingleValueParameterWithDefaultValueBuilder<T, B> {
            private final AbstractSingleUriParameter parameter;
            private final B parentBuilder;
            private final UriPathSegmentActionMapper targetMapper;

            private SingleValueParameterWithDefaultValueBuilder(final B parentBuilder,
                                                                final String id,
                                                                final Class<T> forType,
                                                                final UriPathSegmentActionMapper targetMapper) {
                this.parentBuilder = parentBuilder;
                this.targetMapper = targetMapper;
                parameter = SingleValuedParameterFactory.createUriParameter(id, forType);
            }

            @SuppressWarnings("unchecked")
            public B usingDefaultValue(final T defaultValue) {
                LOG.debug("Registering parameter {} on mapper {} with default value='{}'", parameter, targetMapper, defaultValue);
                parameter.setOptional(defaultValue);
                return noDefault();
            }

            public B noDefault() {
                if (!parameter.isOptional()) {
                    LOG.debug("Registering parameter {} on mapper {} with no default value", parameter, targetMapper);
                }
                targetMapper.registerURIParameter(parameter);
                return parentBuilder;
            }
        }
    }

    public static class SubtreeMapperBuilder {
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private final UriActionMapperTree uriActionMapperTree;
        private final MapperTreeBuilder parentMapperTreeBuilder;

        private SubtreeMapperBuilder(final UriActionMapperTree uriActionMapperTree,
                                     final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                     final MapperTreeBuilder parentMapperTreeBuilder) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.dispatchingMapper = dispatchingMapper;
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
        }

        public SingleValuedParameterBuilder<SubtreeMapperBuilder> withSingleValuedParameter(final String id) {
            return new SingleValuedParameterBuilder<>(this, id, dispatchingMapper);
        }

        public SubtreeMapperBuilder withParameter(final UriParameter<?> parameter) {
            if (parameter.isOptional()) {
                LOG.debug("Registering parameter {} on mapper {} with default value '{}'", parameter, dispatchingMapper,
                        parameter.getDefaultValue());
            } else {
                LOG.debug("Registering parameter {} on mapper {} with no default value", parameter, dispatchingMapper,
                        parameter.getDefaultValue());
            }
            dispatchingMapper.registerURIParameter(parameter);
            return this;
        }

        public SubtreeMapperBuilder onAction(final Class<? extends UriActionCommand> actionCommandClass) {
            dispatchingMapper.setActionCommandClass(actionCommandClass);
            return this;
        }

        public MapperTreeBuilder onSubtree() {
            return new MapperTreeBuilder(uriActionMapperTree, dispatchingMapper, parentMapperTreeBuilder);
        }
    }
}
