package org.roklib.webapps.uridispatching;

import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roland Krüger
 */
public class URIActionMapperTree {

    private URIActionDispatcher dispatcher;

    private URIActionMapperTree() {
        dispatcher = new URIActionDispatcher();
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

    public void interpretFragment(final String fragment) {
        dispatcher.handleURIAction(fragment);
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
