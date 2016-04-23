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
 * This class is the central entry point into the URI fragment routing mechanism.
 * <p>
 * The action dispatcher manages one internal root URI action mapper which dispatches to its sub-mappers. When a URI
 * fragment is to be interpreted, this fragment has to be passed to method {@link #interpretFragment(String)} or {@link
 * #interpretFragment(String, Object)}. The URI fragment is then split into a token list to be recursively interpreted
 * by the registered action mappers. The strategy for splitting a URI fragment into a URI token list is determined by
 * the {@link UriTokenExtractionStrategy} used for this mapper tree. For example, if the following URI is to be
 * interpreted
 * <pre>
 * http://www.example.com/myapp#!/user/home/messages
 * </pre>
 * with the web application running under context <code>http://www.example.com/myapp/</code> the URI fragment to be
 * interpreted is <code>/user/home/messages</code>. This is split into three individual URI tokens <code>user</code>,
 * <code>home</code>, and <code>messages</code>, in that order. To interpret these tokens, the root action mapper passes
 * this URI token list to that sub-mapper which is responsible for handling the first token <code>user</code>. The URI
 * token list is thus interpreted recursively by the sub-mappers of the mapper tree until eventually the mapper
 * responsible for the final URI token is reached. This action mapper will return its action command class which can
 * then be executed as the result of interpreting the given URI fragment.
 * <p>
 * If no final mapper could be found for the remaining URI tokens in the list, either nothing is done or the default
 * action command registered with {@link #setDefaultAction(Class)} is executed. It is thus indicated, that the URI
 * fragment could not successfully be interpreted.
 * <p>
 * <h1>URI parameters</h1>
 * <p>
 * <h1>Default action command</h1> A default action command class can be specified with {@link
 * UriActionMapperTree#setDefaultAction(Class)}. This action command will be executed if the interpretation process of
 * some URI fragment did not yield any action class. Such a default action command could be used to show a Page Not
 * Found error page to the user, for example.
 * <p>
 * <h1>Parameter mode</h1> By default, if not specified differently, the {@link ParameterMode} used by the {@link
 * UriActionMapperTree} is {@link ParameterMode#DIRECTORY_WITH_NAMES}.
 * <p>
 * <h1>URI token and query parameter extraction strategy</h1> By default, the {@link UriActionMapperTree} uses the two
 * standard implementations of the interfaces {@link UriTokenExtractionStrategy} and {@link
 * QueryParameterExtractionStrategy}. These are {@link DirectoryStyleUriTokenExtractionStrategyImpl} and {@link
 * StandardQueryNotationQueryParameterExtractionStrategyImpl}, respectively.
 * <p>
 * <h1>Routing context</h1>
 * <p>
 * <h1>Thread safety</h1> The URI fragment routing framework is thread-safe. This means that you typically have one
 * application-scoped instance of a {@link UriActionMapperTree} which contains all available URI fragments handled by an
 * application.
 * <p>
 * <h1>Constructing a URI action mapper tree with a builder</h1>
 */
public class UriActionMapperTree {

    private static final Logger LOG = LoggerFactory.getLogger(UriActionMapperTree.class);

    private ParameterMode parameterMode = ParameterMode.DIRECTORY_WITH_NAMES;
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;
    private Class<? extends UriActionCommand> defaultAction;

    /**
     * Base dispatching mapper that contains all action mappers at root level.
     */
    private final DispatchingUriPathSegmentActionMapper rootMapper;
    /**
     * Set comprising the mapper names of all action mappers contained in this action dispatcher.
     */
    private Set<String> mapperNamesInUse;

    private UriActionMapperTree() {
        queryParameterExtractionStrategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
        uriTokenExtractionStrategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
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

    public UriActionCommand interpretFragment(String uriFragment) {
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
    public <C> UriActionCommand interpretFragment(String uriFragment, C context) {
        CapturedParameterValues capturedParameterValues = new CapturedParameterValues();
        Class<? extends UriActionCommand> actionCommandClass = getActionForUriFragment(capturedParameterValues,
                uriFragment,
                uriTokenExtractionStrategy.extractUriTokens(queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(uriFragment)),
                queryParameterExtractionStrategy.extractQueryParameters(uriFragment),
                parameterMode);

        if (actionCommandClass != null) {
            UriActionCommand actionCommandObject = createAndConfigureUriActionCommand(uriFragment, context, capturedParameterValues, actionCommandClass);
            actionCommandObject.run();
            return actionCommandObject;
        }
        return null;
    }

    private <C> UriActionCommand createAndConfigureUriActionCommand(String uriFragment, C context, CapturedParameterValues capturedParameterValues, Class<? extends UriActionCommand> actionCommandClass) {
        ActionCommandFactory<C> factory = new ActionCommandFactory<>(actionCommandClass);
        UriActionCommand actionCommandObject = factory.createCommand();
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
    private void setParameterMode(ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

    private void setQueryParameterExtractionStrategy(QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
        Preconditions.checkNotNull(queryParameterExtractionStrategy);
        this.queryParameterExtractionStrategy = queryParameterExtractionStrategy;
    }

    private void setUriTokenExtractionStrategy(UriTokenExtractionStrategy uriTokenExtractionStrategy) {
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

    public String assembleUriFragment(UriPathSegmentActionMapper forMapper) {
        return assembleUriFragment(new CapturedParameterValues(), forMapper);
    }

    public String assembleUriFragment(CapturedParameterValues capturedParameterValues, UriPathSegmentActionMapper forMapper) {
        Preconditions.checkNotNull(forMapper);
        Stack<UriPathSegmentActionMapper> mapperStack = buildMapperStack(forMapper);

        List<String> uriTokens = new LinkedList<>();
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

    private Stack<UriPathSegmentActionMapper> buildMapperStack(UriPathSegmentActionMapper forMapper) {
        Stack<UriPathSegmentActionMapper> stack = new Stack<>();

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

    public List<String> getMapperOverview() {
        List<String> result = new LinkedList<>();
        getRootActionMapper()
                .getSubMapperMap()
                .values()
                .stream()
                .forEach(mapper -> mapper.getMapperOverview("", result));
        return result;
    }

    /**
     * Print the mapper overview created with {@link #getMapperOverview()} to the given {@link PrintStream}.
     *
     * @param target
     */
    public void print(PrintStream target) {
        getMapperOverview().forEach(target::println);
    }

    public static class UriActionMapperTreeBuilder {
        final UriActionMapperTree uriActionMapperTree;

        private UriActionMapperTreeBuilder() {
            uriActionMapperTree = new UriActionMapperTree();
        }

        public MapperTreeBuilder buildMapperTree() {
            return new MapperTreeBuilder(uriActionMapperTree, uriActionMapperTree.getRootActionMapper());
        }

        public UriActionMapperTreeBuilder useUriTokenExtractionStrategy(UriTokenExtractionStrategy uriTokenExtractionStrategy) {
            uriActionMapperTree.setUriTokenExtractionStrategy(uriTokenExtractionStrategy);
            return this;
        }

        public UriActionMapperTreeBuilder useQueryParameterExtractionStrategy(QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
            uriActionMapperTree.setQueryParameterExtractionStrategy(queryParameterExtractionStrategy);
            return this;
        }

        public UriActionMapperTreeBuilder useParameterMode(ParameterMode parameterMode) {
            uriActionMapperTree.setParameterMode(parameterMode);
            return this;
        }

        public UriActionMapperTreeBuilder useDefaultActionCommand(Class<? extends UriActionCommand> defaultActionCommandClass) {
            uriActionMapperTree.setDefaultAction(defaultActionCommandClass);
            return this;
        }
    }

    public static class MapperTreeBuilder {
        private UriActionMapperTree uriActionMapperTree;
        private DispatchingUriPathSegmentActionMapper currentDispatchingMapper;
        private MapperTreeBuilder parentBuilder;

        private MapperTreeBuilder(UriActionMapperTree uriActionMapperTree,
                                  DispatchingUriPathSegmentActionMapper currentDispatchingMapper,
                                  MapperTreeBuilder parentBuilder) {
            this(uriActionMapperTree, currentDispatchingMapper);
            this.parentBuilder = parentBuilder;
        }

        private MapperTreeBuilder(UriActionMapperTree uriActionMapperTree, DispatchingUriPathSegmentActionMapper currentDispatchingMapper) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.currentDispatchingMapper = currentDispatchingMapper;
        }

        public UriActionMapperTree build() {
            return uriActionMapperTree;
        }

        public MapperTreeBuilder finishMapper() {
            return parentBuilder == null ? this : parentBuilder;
        }

        public MapperTreeBuilder addMapper(UriPathSegmentActionMapper mapper) {
            currentDispatchingMapper.addSubMapper(mapper);
            return this;
        }

        public MapperBuilder map(String segmentName) {
            return new MapperBuilder(this, currentDispatchingMapper, segmentName);
        }

        public SubtreeMapperBuilder mapSubtree(String mapperName, String segmentName, Consumer<DispatchingUriPathSegmentActionMapper> consumer) {
            SubtreeMapperBuilder subtreeBuilder = mapSubtree(mapperName, segmentName);
            consumer.accept(subtreeBuilder.dispatchingMapper);
            return subtreeBuilder;
        }

        public SubtreeMapperBuilder mapSubtree(String mapperName, Consumer<DispatchingUriPathSegmentActionMapper> consumer) {
            return mapSubtree(mapperName, mapperName, consumer);
        }

        public SubtreeMapperBuilder mapSubtree(String mapperName, String segmentName) {
            final DispatchingUriPathSegmentActionMapper dispatchingMapper = new DispatchingUriPathSegmentActionMapper(mapperName, segmentName);
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }

        public SubtreeMapperBuilder mapSubtree(String mapperName) {
            return mapSubtree(mapperName, mapperName);
        }

        public SubtreeMapperBuilder mapSubtree(DispatchingUriPathSegmentActionMapper dispatchingMapper) {
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }
    }

    public static class MapperBuilder {
        private MapperTreeBuilder parentMapperTreeBuilder;
        private DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private String mapperName;
        private String pathSegment;

        private MapperBuilder(MapperTreeBuilder parentMapperTreeBuilder,
                              DispatchingUriPathSegmentActionMapper dispatchingMapper,
                              String mapperName) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.mapperName = mapperName;
        }

        public SimpleMapperParameterBuilder onAction(Class<? extends UriActionCommand> actionCommandClass) {
            Preconditions.checkNotNull(actionCommandClass);
            SimpleUriPathSegmentActionMapper mapper = new SimpleUriPathSegmentActionMapper(mapperName, pathSegment, actionCommandClass);
            return new SimpleMapperParameterBuilder(parentMapperTreeBuilder, dispatchingMapper, mapper);
        }

        public MapperBuilder onPathSegment(String pathSegment) {
            this.pathSegment = pathSegment;
            return this;
        }
    }

    public static class SimpleMapperParameterBuilder {
        private MapperTreeBuilder parentMapperTreeBuilder;
        private SimpleUriPathSegmentActionMapper targetMapper;
        private DispatchingUriPathSegmentActionMapper dispatchingMapper;

        private SimpleMapperParameterBuilder(MapperTreeBuilder parentMapperTreeBuilder,
                                             DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                             SimpleUriPathSegmentActionMapper targetMapper) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.targetMapper = targetMapper;
        }

        public SingleValuedParameterBuilder<SimpleMapperParameterBuilder> withSingleValuedParameter(String id) {
            return new SingleValuedParameterBuilder<>(this, id, targetMapper);
        }

        public SimpleMapperParameterBuilder withParameter(UriParameter<?> parameter) {
            targetMapper.registerURIParameter(parameter);
            return this;
        }

        public MapperTreeBuilder finishMapper() {
            dispatchingMapper.addSubMapper(targetMapper);
            return parentMapperTreeBuilder;
        }

        public MapperTreeBuilder finishMapper(Consumer<SimpleUriPathSegmentActionMapper> consumer) {
            consumer.accept(targetMapper);
            return finishMapper();
        }
    }

    public static class SingleValuedParameterBuilder<B> {
        private B parentBuilder;
        private String id;
        private UriPathSegmentActionMapper targetMapper;

        private SingleValuedParameterBuilder(B parentBuilder, String id,
                                             UriPathSegmentActionMapper targetMapper) {
            this.parentBuilder = parentBuilder;
            this.id = id;
            this.targetMapper = targetMapper;
        }

        public <T> SingleValueParameterWithDefaultValueBuilder<T, B> forType(Class<T> forType) {
            return new SingleValueParameterWithDefaultValueBuilder<>(parentBuilder, id, forType, targetMapper);
        }

        public static class SingleValueParameterWithDefaultValueBuilder<T, B> {
            private AbstractSingleUriParameter parameter;
            private B parentBuilder;
            private UriPathSegmentActionMapper targetMapper;

            private SingleValueParameterWithDefaultValueBuilder(B parentBuilder,
                                                                String id,
                                                                Class<T> forType,
                                                                UriPathSegmentActionMapper targetMapper) {
                this.parentBuilder = parentBuilder;
                this.targetMapper = targetMapper;
                parameter = SingleValuedParameterFactory.createUriParameter(id, forType);
            }

            @SuppressWarnings("unchecked")
            public B usingDefaultValue(T defaultValue) {
                parameter.setOptional(defaultValue);
                return noDefault();
            }

            public B noDefault() {
                targetMapper.registerURIParameter(parameter);
                return parentBuilder;
            }
        }
    }

    public static class SubtreeMapperBuilder {
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private UriActionMapperTree uriActionMapperTree;
        private MapperTreeBuilder parentMapperTreeBuilder;

        private SubtreeMapperBuilder(UriActionMapperTree uriActionMapperTree,
                                     DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                     MapperTreeBuilder parentMapperTreeBuilder) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.dispatchingMapper = dispatchingMapper;
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
        }

        public SingleValuedParameterBuilder<SubtreeMapperBuilder> withSingleValuedParameter(String id) {
            return new SingleValuedParameterBuilder<>(this, id, dispatchingMapper);
        }

        public SubtreeMapperBuilder withParameter(UriParameter<?> parameter) {
            dispatchingMapper.registerURIParameter(parameter);
            return this;
        }

        public SubtreeMapperBuilder onAction(Class<? extends UriActionCommand> actionCommandClass) {
            dispatchingMapper.setActionCommandClass(actionCommandClass);
            return this;
        }

        public MapperTreeBuilder onSubtree() {
            return new MapperTreeBuilder(uriActionMapperTree, dispatchingMapper, parentMapperTreeBuilder);
        }
    }
}
