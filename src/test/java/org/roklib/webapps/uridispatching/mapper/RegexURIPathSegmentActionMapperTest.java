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
import org.roklib.webapps.uridispatching.URIActionDispatcher;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegexURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testActionHandler;
    private TURIActionCommand testActionCommand;
    private TURIPathSegmentActionMapper lastActionHandler;
    private TURIActionCommand lastActionCommand;
    private org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper middleActionHandler;
    private TURIActionCommand middleActionCommand;
    private TURIActionCommand regexActionCommand1;
    private org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper regexActionHandler1;
    private TURIActionCommand regexActionCommand2;
    private org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper regexActionHandler2;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher(false);

        testActionCommand = new TURIActionCommand();
        testActionHandler = new TURIPathSegmentActionMapper("1test_x", testActionCommand);

        regexActionCommand1 = new TURIActionCommand();
        regexActionCommand2 = new TURIActionCommand();

        // first regex action handler is responsible for URIs like '1test_abc' or '2test_123test'
        regexActionHandler1 = new org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper("(\\d)test_(.*)");
        regexActionHandler1.setActionCommand(regexActionCommand1);

        // second regex action handler is responsible for URIs like '3test_5xxx' or '12test_9yyy'
        regexActionHandler2 = new org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper("(\\d{1,2})test_(\\d\\w+)");
        regexActionHandler2.setActionCommand(regexActionCommand2);

        lastActionCommand = new TURIActionCommand();
        lastActionHandler = new TURIPathSegmentActionMapper("last", lastActionCommand);

        middleActionCommand = new TURIActionCommand();
        middleActionHandler = new org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper("middle");
        middleActionHandler.setActionCommand(middleActionCommand);

        regexActionHandler2.addSubMapper(middleActionHandler);
        middleActionHandler.addSubMapper(lastActionHandler);

        dispatcher.addURIPathSegmentMapper(regexActionHandler1);
        dispatcher.addURIPathSegmentMapper(testActionHandler);
        dispatcher.addURIPathSegmentMapper(regexActionHandler2); // add second regex handler last, so that it has
        // least precedence
    }

    @Test
    public void testDispatching() {
        dispatcher.handleURIAction("/23test_123/middle/last");
        assertActionCommandWasExecuted(lastActionCommand);
    }

    @Test
    public void testURLDecoding() {
        dispatcher.handleURIAction("/3test_%22xx+xx%22");
        assertActionCommandWasExecuted(regexActionCommand1);
        assertMatchedTokenFragments(regexActionHandler1, new String[]{"3", "\"xx xx\""});
    }

    @Test
    public void testCaseInsensitive() {
        dispatcher.setCaseSensitive(false);
        dispatcher.handleURIAction("/1TEST_x");

        // the dispatching action handler is added second to the dispatcher, but it has highest
        // precedence
        assertActionCommandWasExecuted(testActionCommand);
        resetActionCommands();

        dispatcher.handleURIAction("/2TEST_2x");
        assertActionCommandWasExecuted(regexActionCommand1);
        assertMatchedTokenFragments(regexActionHandler1, new String[]{"2", "2x"});
        resetActionCommands();

        dispatcher.handleURIAction("12TEST_2xxx");
        assertActionCommandWasExecuted(regexActionCommand2);
        assertMatchedTokenFragments(regexActionHandler2, new String[]{"12", "2xxx"});
    }

    @Test
    public void testCaseSensitive() {
        dispatcher.setCaseSensitive(true);

        dispatcher.handleURIAction("/1test_x");

        // the dispatching action handler is added second to the dispatcher, but it has highest
        // precedence
        assertActionCommandWasExecuted(testActionCommand);
        resetActionCommands();

        dispatcher.handleURIAction("/2test_abc");
        assertActionCommandWasExecuted(regexActionCommand1);
        assertMatchedTokenFragments(regexActionHandler1, new String[]{"2", "abc"});
        resetActionCommands();

        dispatcher.handleURIAction("12test_2xxx");
        assertActionCommandWasExecuted(regexActionCommand2);
        assertMatchedTokenFragments(regexActionHandler2, new String[]{"12", "2xxx"});
    }

    @Test
    public void testGetParameterizedActionURI() {
        regexActionHandler2.setURIToken("17test_23some_value");
        assertEquals("/17test_23some_value/middle/last", lastActionHandler.getParameterizedActionURI(true).toString());
        regexActionHandler2.setURIToken("99test_9999");
        assertEquals("/99test_9999/middle/last", lastActionHandler.getParameterizedActionURI(true).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetActionURI_Failure() {
        regexActionHandler2.setURIToken("does_not_match_with_regex");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper("  ");
    }

    @Test
    public void testGetMatchedTokenFragmentCount() {
        // if a regex action handler has not been evaluated yet, its matched token fragment count is 0,
        // even if the underlying array is still null
        assertEquals(0, regexActionHandler2.getMatchedTokenFragmentCount());
        dispatcher.handleURIAction("12test_2xxx");
        assertEquals(2, regexActionHandler2.getMatchedTokenFragmentCount());
    }

    private void assertActionCommandWasExecuted(TURIActionCommand command) {
        assertTrue(command.executed);
    }

    private void assertMatchedTokenFragments(org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapper handler, String[] expectedTokenFragments) {
        assertEquals(expectedTokenFragments.length, handler.getMatchedTokenFragmentCount());
        assertTrue(Arrays.equals(expectedTokenFragments, handler.getMatchedTokenFragments()));
    }

    private void resetActionCommands() {
        testActionCommand.executed = false;
        regexActionCommand1.executed = false;
        regexActionCommand2.executed = false;
    }

}
