package org.roklib.webapps.uridispatching;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper.ParameterMode;
import org.roklib.webapps.uridispatching.parameter.AbstractSingleUriParameter;
import org.roklib.webapps.uridispatching.parameter.SingleValuedParameterFactory;
import org.roklib.webapps.uridispatching.parameter.UriParameter;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValues;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.strategy.DirectoryStyleUriTokenExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.QueryParameterExtractionStrategy;
import org.roklib.webapps.uridispatching.strategy.StandardQueryNotationQueryParameterExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.UriTokenExtractionStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * @author Roland Krüger
 */
public class UriActionMapperTree {

    private UriActionDispatcher dispatcher;
    private ParameterMode parameterMode = ParameterMode.DIRECTORY_WITH_NAMES;
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;

    private UriActionMapperTree() {
        dispatcher = new UriActionDispatcher();
        queryParameterExtractionStrategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
        uriTokenExtractionStrategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
    }

    /**
     * This method is the central entry point for the URI action handling framework.
     *
     * @param uriFragment relative URI to be interpreted by the URI action handling framework. This may be an URI such as
     *                    <code>/admin/configuration/settings?language=de</code>
     * @return the command responsible for the given <code>uriFragment</code> or <code>null</code> if
     * the fragment could not be resolved to any command
     */
    public UriActionCommand interpretFragment(String uriFragment) {
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        Class<? extends UriActionCommand> actionCommandClass = dispatcher.getActionForUriFragment(capturedParameterValues,
                uriFragment,
                uriTokenExtractionStrategy.extractUriTokens(queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(uriFragment)),
                queryParameterExtractionStrategy.extractQueryParameters(uriFragment),
                parameterMode);

        if (actionCommandClass != null) {
            UriActionCommand actionCommandObject = capturedParameterValues.createActionCommandAndPassParameters(uriFragment, actionCommandClass);
            actionCommandObject.run();
            return actionCommandObject;
        }
        return null;
    }

    /**
     * Set the parameter mode to be used for interpreting the visited URIs.
     *
     * @param parameterMode {@link ParameterMode} which will be used by {@link #interpretFragment(String)}
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
    Collection<AbstractUriPathSegmentActionMapper> getRootActionMappers() {
        return dispatcher.getRootActionMapper().getSubMapperMap().values();
    }

    public String assembleUriFragment(CapturedParameterValues capturedParameterValues, AbstractUriPathSegmentActionMapper forMapper) {
        Preconditions.checkNotNull(forMapper);
        Stack<AbstractUriPathSegmentActionMapper> mapperStack = buildMapperStack(forMapper);
        String queryParamSection = "";
        if (parameterMode == ParameterMode.QUERY) {
            queryParamSection = queryParameterExtractionStrategy.assembleQueryParameterSectionForUriFragment(capturedParameterValues.asQueryParameterMap());
        }

        StringBuilder fragmentBuilder = new StringBuilder();

        List<String> uriTokens = new LinkedList<>();
        while (!mapperStack.isEmpty()) {
            final AbstractUriPathSegmentActionMapper mapper = mapperStack.pop();
            mapper.assembleUriFragmentTokens(capturedParameterValues, uriTokens, parameterMode);
        }

        fragmentBuilder.append(uriTokenExtractionStrategy.assembleUriFragmentFromTokens(uriTokens));
        fragmentBuilder.append(queryParamSection);
        return fragmentBuilder.toString();
    }

    private Stack<AbstractUriPathSegmentActionMapper> buildMapperStack(AbstractUriPathSegmentActionMapper forMapper) {
        Stack<AbstractUriPathSegmentActionMapper> stack = new Stack<>();

        AbstractUriPathSegmentActionMapper currentMapper = forMapper;
        do {
            stack.push(currentMapper);
            currentMapper = currentMapper.getParentMapper();
            if (currentMapper == null) {
                throw new IllegalArgumentException("given mapper instance is not part of the mapper tree");
            }
        } while (currentMapper != dispatcher.getRootActionMapper());

        return stack;
    }

    public static class UriActionMapperTreeBuilder {
        final UriActionMapperTree uriActionMapperTree;

        private UriActionMapperTreeBuilder() {
            uriActionMapperTree = new UriActionMapperTree();
        }

        public MapperTreeBuilder buildMapperTree() {
            return new MapperTreeBuilder(uriActionMapperTree, uriActionMapperTree.dispatcher.getRootActionMapper());
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

        public MapperTreeBuilder addMapper(AbstractUriPathSegmentActionMapper mapper) {
            currentDispatchingMapper.addSubMapper(mapper);
            return this;
        }

        public MapperBuilder map(String segmentName) {
            return new MapperBuilder(this, currentDispatchingMapper, segmentName);
        }

        public SubtreeMapperBuilder mapSubtree(String segmentName, Consumer<DispatchingUriPathSegmentActionMapper> consumer) {
            SubtreeMapperBuilder subtreeBuilder = mapSubtree(segmentName);
            consumer.accept(subtreeBuilder.dispatchingMapper);
            return subtreeBuilder;
        }

        public SubtreeMapperBuilder mapSubtree(String segmentName) {
            final DispatchingUriPathSegmentActionMapper dispatchingMapper = new DispatchingUriPathSegmentActionMapper(segmentName);
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }

        public SubtreeMapperBuilder mapSubtree(DispatchingUriPathSegmentActionMapper dispatchingMapper) {
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }
    }

    public static class MapperBuilder {
        private MapperTreeBuilder parentMapperTreeBuilder;
        private DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private SimpleUriPathSegmentActionMapper mapper;

        private MapperBuilder(MapperTreeBuilder parentMapperTreeBuilder,
                              DispatchingUriPathSegmentActionMapper dispatchingMapper,
                              String segmentName) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            mapper = new SimpleUriPathSegmentActionMapper(segmentName);
        }

        public SimpleMapperParameterBuilder onAction(Class<? extends UriActionCommand> actionCommandClass) {
            mapper.setActionCommandClass(actionCommandClass);
            return new SimpleMapperParameterBuilder(parentMapperTreeBuilder, dispatchingMapper, mapper);
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
