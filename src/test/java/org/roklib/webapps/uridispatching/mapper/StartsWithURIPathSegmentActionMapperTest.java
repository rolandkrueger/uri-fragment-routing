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

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class StartsWithURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testActionHandler;
    private Class<? extends URIActionCommand> testActionCommand;
    private org.roklib.webapps.uridispatching.mapper.StartsWithURIPathSegmentActionMapper startsWithActionHandler;
    private Class<? extends URIActionCommand> startsWithActionCommand;
    private TURIPathSegmentActionMapper lastActionHandler;
    private Class<? extends URIActionCommand> lastActionCommand;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher(false);

        testActionCommand = TURIActionCommand.class;
        testActionHandler = new TURIPathSegmentActionMapper("testhandler", testActionCommand);

        startsWithActionHandler = new org.roklib.webapps.uridispatching.mapper.StartsWithURIPathSegmentActionMapper("test");
        startsWithActionCommand = TURIActionCommand.class;
        startsWithActionHandler.setActionCommand(startsWithActionCommand);

        lastActionCommand = TURIActionCommand.class;
        lastActionHandler = new TURIPathSegmentActionMapper("last", lastActionCommand);
        startsWithActionHandler.addSubMapper(lastActionHandler);

        dispatcher.addURIPathSegmentMapper(startsWithActionHandler);
        dispatcher.addURIPathSegmentMapper(testActionHandler);
    }

    @Test
    public void testDispatching() {
        dispatcher.handleURIAction("/testvalue/last");
        assertActionCommandWasExecuted(lastActionCommand);
    }

    @Test
    public void testPrecedence() {
        dispatcher.handleURIAction("/testhandler");
        assertActionCommandWasExecuted(testActionCommand);
    }

    @Test
    public void testCaseSensitive() {
        dispatcher.setCaseSensitive(true);
        dispatcher.handleURIAction("/testvalue");
        assertOutcome();
    }

    @Test
    public void testCaseInsensitive() {
        dispatcher.setCaseSensitive(false);
        dispatcher.handleURIAction("/TESTvalue");
        assertOutcome();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new org.roklib.webapps.uridispatching.mapper.StartsWithURIPathSegmentActionMapper("  ");
    }

    private void assertOutcome() {
        assertActionCommandWasExecuted(startsWithActionCommand);
        assertMatchedTokenFragments(startsWithActionHandler, new String[]{"value"});
    }

    private void assertActionCommandWasExecuted(Class<? extends URIActionCommand> command) {
        // FIXME
        //assertTrue(command.executed);
    }

    private void assertMatchedTokenFragments(org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper handler, String[] expectedTokenFragments) {
        assertTrue(Arrays.equals(expectedTokenFragments, handler.getMatchedTokenFragments()));
    }
}
