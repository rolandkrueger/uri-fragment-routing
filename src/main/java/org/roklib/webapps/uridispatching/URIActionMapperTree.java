package org.roklib.webapps.uridispatching;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.URIPathSegmentActionMapper.ParameterMode;
import org.roklib.webapps.uridispatching.parameter.value.CapturedParameterValuesImpl;
import org.roklib.webapps.uridispatching.strategy.DirectoryStyleUriTokenExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.QueryParameterExtractionStrategy;
import org.roklib.webapps.uridispatching.strategy.StandardQueryNotationQueryParameterExtractionStrategyImpl;
import org.roklib.webapps.uridispatching.strategy.UriTokenExtractionStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Roland Krüger
 */
public class URIActionMapperTree {

    private URIActionDispatcher dispatcher;
    private ParameterMode parameterMode = ParameterMode.QUERY;
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;

    private URIActionMapperTree() {
        dispatcher = new URIActionDispatcher();
        queryParameterExtractionStrategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
        uriTokenExtractionStrategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
    }

    /**
     * This method is the central entry point for the URI action handling framework.
     *
     * @param uriFragment relative URI to be interpreted by the URI action handling framework. This may be an URI such as
     *                    <code>/admin/configuration/settings/language/de</code>
     */
    public void interpretFragment(String uriFragment) {
        Map<String, List<String>> extractQueryParameters = queryParameterExtractionStrategy.extractQueryParameters(uriFragment);
        CapturedParameterValuesImpl capturedParameterValues = new CapturedParameterValuesImpl();
        Class<? extends URIActionCommand> action = dispatcher.getActionForUriFragment(capturedParameterValues, queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(uriFragment), uriTokenExtractionStrategy.extractUriTokens(uriFragment), extractQueryParameters, parameterMode);
        if (action != null) {
            URIActionCommand actionCommandObject = capturedParameterValues.createActionCommandAndPassParameters(uriFragment, action);
            actionCommandObject.execute();
        }
    }

    /**
     * Set the parameter mode to be used for interpreting the visited URIs.
     *
     * @param parameterMode {@link ParameterMode} which will be used by {@link #interpretFragment(String)}
     */
    public void setParameterMode(ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

    public void setQueryParameterExtractionStrategy(QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
        Preconditions.checkNotNull(queryParameterExtractionStrategy);
        this.queryParameterExtractionStrategy = queryParameterExtractionStrategy;
    }

    public void setUriTokenExtractionStrategy(UriTokenExtractionStrategy uriTokenExtractionStrategy) {
        Preconditions.checkNotNull(uriTokenExtractionStrategy);
        this.uriTokenExtractionStrategy = uriTokenExtractionStrategy;
    }

    public static URIActionMapperTreeBuilder create() {
        return new URIActionMapperTreeBuilder();
    }

    public static URIPathSegmentBuilder pathSegment(String segment) {
        return new URIPathSegmentBuilder(segment);
    }

    public static URIActionCommandBuilder action(final Class<? extends URIActionCommand> command) {
        return new URIActionCommandBuilder(command);
    }

    public static SubtreeActionMapperBuilder subtree() {
        return new SubtreeActionMapperBuilder();
    }

    public Collection<AbstractURIPathSegmentActionMapper> getRootActionMappers() {
        return dispatcher.getRootActionMapper().getSubMapperMap().values(); // TODO: refactor
    }

    public AbstractURIPathSegmentActionMapper getRootActionMapper(final String segmentName) {
        return dispatcher.getRootActionMapper().getSubMapperMap().get(segmentName); // TODO: refactor
    }

    public static class URIActionMapperTreeBuilder {
        private SubtreeActionMapperBuilder subtreeActionMapperBuilder = new SubtreeActionMapperBuilder();

        public URIActionMapperTree build() {
            return addMappersFromBuilderToMapperTreeRoot(new URIActionMapperTree());
        }

        private URIActionMapperTree addMappersFromBuilderToMapperTreeRoot(final URIActionMapperTree uriActionMapperTree) {
            subtreeActionMapperBuilder.build(uriActionMapperTree.dispatcher.getRootActionMapper());
            return uriActionMapperTree;
        }

        public URIActionMapperTreeBuilder map(URIPathSegmentActionMapperBuilder pathSegmentBuilder) {
            subtreeActionMapperBuilder.builders.add(pathSegmentBuilder);
            return this;
        }
    }

    public static class URIPathSegmentBuilder {
        private String segmentName;

        public URIPathSegmentBuilder(final String segmentName) {
            Preconditions.checkNotNull(segmentName);
            this.segmentName = segmentName;
        }

        public URIPathSegmentActionMapperBuilder on(final URIActionCommandBuilder actionBuilder) {
            return new URIPathSegmentActionMapperBuilder(segmentName, actionBuilder);
        }

        public URIPathSegmentActionMapperBuilder on(final SubtreeActionMapperBuilder subtreeBuilder) {
            return new URIPathSegmentActionMapperBuilder(segmentName, subtreeBuilder);
        }
    }

    public static class URIPathSegmentActionMapperBuilder {
        private AbstractURIPathSegmentActionMapper mapper;

        public URIPathSegmentActionMapperBuilder(final String segmentName, final URIActionCommandBuilder actionBuilder) {
            mapper = new SimpleURIPathSegmentActionMapper(segmentName);
            mapper.setActionCommandClass(actionBuilder.getCommand());
        }

        public URIPathSegmentActionMapperBuilder(final String segmentName, final SubtreeActionMapperBuilder subtreeBuilder) {
            mapper = subtreeBuilder.build(new DispatchingURIPathSegmentActionMapper(segmentName));
        }

        public AbstractURIPathSegmentActionMapper getMapper() {
            return mapper;
        }
    }

    public static class URIActionCommandBuilder {

        private final Class<? extends URIActionCommand> command;

        public URIActionCommandBuilder(final Class<? extends URIActionCommand> command) {
            this.command = command;
        }

        public Class<? extends URIActionCommand> getCommand() {
            return command;
        }
    }

    public static class SubtreeActionMapperBuilder {
        private List<URIPathSegmentActionMapperBuilder> builders = new LinkedList<>();
        private Class<? extends URIActionCommand> actionCommand;

        private AbstractURIPathSegmentActionMapper build(final DispatchingURIPathSegmentActionMapper mapper) {
            addSubMappers(mapper);
            setActionCommandIfDefined(mapper);
            return mapper;
        }

        private void setActionCommandIfDefined(final DispatchingURIPathSegmentActionMapper mapper) {
            if (actionCommand != null) {
                mapper.setActionCommandClass(actionCommand);
            }
        }

        private void addSubMappers(final DispatchingURIPathSegmentActionMapper mapper) {
            for (URIPathSegmentActionMapperBuilder builder : builders) {
                mapper.addSubMapper(builder.getMapper());
            }
        }

        public SubtreeActionMapperBuilder map(URIPathSegmentActionMapperBuilder pathSegmentBuilder) {
            builders.add(pathSegmentBuilder);
            return this;
        }

        public SubtreeActionMapperBuilder withActionCommand(final Class<? extends URIActionCommand> actionCommand) {
            this.actionCommand = actionCommand;
            return this;
        }
    }
}
