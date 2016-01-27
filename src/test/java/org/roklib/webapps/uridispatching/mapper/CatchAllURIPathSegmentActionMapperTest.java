/*
 * Copyright (C) 2007 Roland Krueger Created on 22.09.2012
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
package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.TURIActionCommand;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.URIActionDispatcher;

import static org.junit.Assert.assertEquals;

public class CatchAllURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testActionHandler;
    private Class<? extends URIActionCommand> testActionCommand;
    private org.roklib.webapps.uridispatching.mapper.CatchAllURIPathSegmentActionMapper catchAllActionHandler;
    private Class<? extends URIActionCommand> catchAllActionCommand;
    private TURIPathSegmentActionMapper lastActionHandler;
    private Class<? extends URIActionCommand> lastActionCommand;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher(false);

        testActionCommand = TURIActionCommand.class;
        testActionHandler = new TURIPathSegmentActionMapper("test", testActionCommand);

        catchAllActionHandler = new org.roklib.webapps.uridispatching.mapper.CatchAllURIPathSegmentActionMapper();
        catchAllActionCommand = TURIActionCommand.class;
        catchAllActionHandler.setActionCommand(catchAllActionCommand);

        lastActionCommand = TURIActionCommand.class;
        lastActionHandler = new TURIPathSegmentActionMapper("last", lastActionCommand);
        catchAllActionHandler.addSubMapper(lastActionHandler);

        dispatcher.addURIPathSegmentMapper(catchAllActionHandler);
        dispatcher.addURIPathSegmentMapper(testActionHandler);
    }

    @Test
    public void test() {
        dispatcher.handleURIAction("/test");
        assertActionCommandWasExecuted(testActionCommand);

        dispatcher.handleURIAction("/someurlfragment");
        assertActionCommandWasExecuted(catchAllActionCommand);
        assertEquals("someurlfragment", catchAllActionHandler.getCurrentURIToken());

        dispatcher.handleURIAction("/anything/last");
        assertActionCommandWasExecuted(lastActionCommand);
        assertEquals("anything", catchAllActionHandler.getCurrentURIToken());
    }

    private void assertActionCommandWasExecuted(Class<? extends URIActionCommand> command) {
        //FIXME
//        assertTrue(command.executed);
    }
}
