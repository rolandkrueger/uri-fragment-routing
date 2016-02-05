package org.roklib.webapps.uridispatching;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleUriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper.ParameterMode;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.strategy.DirectoryStyleUriTokenExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.QueryParameterExtractionStrategy;
import org.roklib.webapps.uridispatching.strategy.StandardQueryNotationQueryParameterExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.UriTokenExtractionStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roland Krüger
 */
public class UriActionMapperTree {

    private UriActionDispatcher dispatcher;
    private ParameterMode parameterMode = ParameterMode.QUERY;
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

    public static UriPathSegmentBuilder pathSegment(String segment) {
        return new UriPathSegmentBuilder(segment);
    }

    public static UriActionCommandBuilder action(final Class<? extends UriActionCommand> command) {
        return new UriActionCommandBuilder(command);
    }

    public static SubtreeActionMapperBuilder subtree() {
        return new SubtreeActionMapperBuilder();
    }

    /**
     * Needed by unit tests.
     */
    Collection<AbstractUriPathSegmentActionMapper> getRootActionMappers() {
        return dispatcher.getRootActionMapper().getSubMapperMap().values();
    }

    public static class UriActionMapperTreeBuilder {
        private SubtreeActionMapperBuilder subtreeActionMapperBuilder = new SubtreeActionMapperBuilder();
        final UriActionMapperTree uriActionMapperTree;

        public UriActionMapperTreeBuilder() {
            uriActionMapperTree = new UriActionMapperTree();
        }

        public UriActionMapperTree build() {
            return addMappersFromBuilderToMapperTreeRoot(uriActionMapperTree);
        }

        private UriActionMapperTree addMappersFromBuilderToMapperTreeRoot(final UriActionMapperTree uriActionMapperTree) {
            subtreeActionMapperBuilder.build(uriActionMapperTree.dispatcher.getRootActionMapper());
            return uriActionMapperTree;
        }

        public UriActionMapperTreeBuilder map(UriPathSegmentActionMapperBuilder pathSegmentBuilder) {
            subtreeActionMapperBuilder.builders.add(pathSegmentBuilder);
            return this;
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

    public static class UriPathSegmentBuilder {
        private String segmentName;

        public UriPathSegmentBuilder(final String segmentName) {
            Preconditions.checkNotNull(segmentName);
            this.segmentName = segmentName;
        }

        public UriPathSegmentActionMapperBuilder on(final UriActionCommandBuilder actionBuilder) {
            return new UriPathSegmentActionMapperBuilder(segmentName, actionBuilder);
        }

        public UriPathSegmentActionMapperBuilder on(final SubtreeActionMapperBuilder subtreeBuilder) {
            return new UriPathSegmentActionMapperBuilder(segmentName, subtreeBuilder);
        }
    }

    private static class UriPathSegmentActionMapperBuilder {
        private AbstractUriPathSegmentActionMapper mapper;

        private UriPathSegmentActionMapperBuilder(final String segmentName, final UriActionCommandBuilder actionBuilder) {
            mapper = new SimpleUriPathSegmentActionMapper(segmentName);
            mapper.setActionCommandClass(actionBuilder.getCommand());
        }

        private UriPathSegmentActionMapperBuilder(final String segmentName, final SubtreeActionMapperBuilder subtreeBuilder) {
            mapper = subtreeBuilder.build(new DispatchingUriPathSegmentActionMapper(segmentName));
        }

        private AbstractUriPathSegmentActionMapper getMapper() {
            return mapper;
        }
    }

    private static class UriActionCommandBuilder {

        private final Class<? extends UriActionCommand> command;

        private UriActionCommandBuilder(final Class<? extends UriActionCommand> command) {
            this.command = command;
        }

        private Class<? extends UriActionCommand> getCommand() {
            return command;
        }
    }

    public static class SubtreeActionMapperBuilder {
        private List<UriPathSegmentActionMapperBuilder> builders = new LinkedList<>();
        private Class<? extends UriActionCommand> actionCommand;

        private AbstractUriPathSegmentActionMapper build(final DispatchingUriPathSegmentActionMapper mapper) {
            addSubMappers(mapper);
            setActionCommandIfDefined(mapper);
            return mapper;
        }

        private void setActionCommandIfDefined(final DispatchingUriPathSegmentActionMapper mapper) {
            if (actionCommand != null) {
                mapper.setActionCommandClass(actionCommand);
            }
        }

        private void addSubMappers(final DispatchingUriPathSegmentActionMapper mapper) {
            for (UriPathSegmentActionMapperBuilder builder : builders) {
                mapper.addSubMapper(builder.getMapper());
            }
        }

        public SubtreeActionMapperBuilder map(UriPathSegmentActionMapperBuilder pathSegmentBuilder) {
            builders.add(pathSegmentBuilder);
            return this;
        }

        public SubtreeActionMapperBuilder withActionCommand(final Class<? extends UriActionCommand> actionCommand) {
            this.actionCommand = actionCommand;
            return this;
        }
    }
}
