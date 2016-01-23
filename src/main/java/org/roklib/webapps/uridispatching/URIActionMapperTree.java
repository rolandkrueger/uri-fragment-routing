/*
 * Copyright (C) 2007 - 2014 Roland Krueger
 *
 * Author: Roland Krueger (www.rolandkrueger.info)
 *
 * This file is part of RoKlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.roklib.webapps.uridispatching;

import org.roklib.util.helper.CheckForNull;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.mapper.SimpleURIPathSegmentActionMapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 2.0
 */
public class URIActionMapperTree {

    private URIActionDispatcher dispatcher;

    private URIActionMapperTree() {
        dispatcher = new URIActionDispatcher(false);
    }

    public static URIActionMapperTreeBuilder create() {
        return new URIActionMapperTreeBuilder();
    }

    public static URIPathSegmentBuilder pathSegment(String segment) {
        return new URIPathSegmentBuilder(segment);
    }

    public static URIActionCommandBuilder action(final AbstractURIActionCommand command) {
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
            CheckForNull.check(segmentName);
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
            mapper.setActionCommand(actionBuilder.getCommand());
        }

        public URIPathSegmentActionMapperBuilder(final String segmentName, final SubtreeActionMapperBuilder subtreeBuilder) {
            mapper = subtreeBuilder.build(new DispatchingURIPathSegmentActionMapper(segmentName));
        }

        public AbstractURIPathSegmentActionMapper getMapper() {
            return mapper;
        }
    }

    public static class URIActionCommandBuilder {

        private final AbstractURIActionCommand command;

        public URIActionCommandBuilder(final AbstractURIActionCommand command) {
            this.command = command;
        }

        public AbstractURIActionCommand getCommand() {
            return command;
        }
    }

    public static class SubtreeActionMapperBuilder {
        private List<URIPathSegmentActionMapperBuilder> builders = new LinkedList<>();
        private AbstractURIActionCommand actionCommand;

        private AbstractURIPathSegmentActionMapper build(final DispatchingURIPathSegmentActionMapper mapper) {
            addSubMappers(mapper);
            setActionCommandIfDefined(mapper);
            return mapper;
        }

        private void setActionCommandIfDefined(final DispatchingURIPathSegmentActionMapper mapper) {
            if (actionCommand != null) {
                mapper.setActionCommand(actionCommand);
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

        public SubtreeActionMapperBuilder withActionCommand(final AbstractURIActionCommand actionCommand) {
            this.actionCommand = actionCommand;
            return this;
        }
    }
}
