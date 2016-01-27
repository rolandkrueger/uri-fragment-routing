/*
 * $Id: AbstractURIActionHandlerTest.java 186 2010-11-01 10:12:14Z roland $
 * Copyright (C) 2007 - 2010 Roland Krueger
 * Created on 17.02.2010 
 * 
 * Author: Roland Krueger (www.rolandkrueger.info) 
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
import org.roklib.webapps.uridispatching.mapper.URIPathSegmentActionMapper.ParameterMode;
import org.roklib.webapps.uridispatching.parameter.SingleBooleanURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

import static org.junit.Assert.*;

public class AbstractURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testHandler1;
    private TURIPathSegmentActionMapper testHandler2;
    private TURIPathSegmentActionMapper testHandler3;
    private org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper dispatchingHandler;
    private TURIActionCommand testCommand1;
    private TURIActionCommand testCommand2;
    private SingleStringURIParameter urlParameter;
    private SingleBooleanURIParameter urlParameter2;
    private org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper caseSensitiveDispatchingHandler;
    private URIActionDispatcher caseSensitiveDispatcher;
    private TURIPathSegmentActionMapper caseSensitiveTestHandler1;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher(false);
        caseSensitiveDispatcher = new URIActionDispatcher(true);

        urlParameter = new SingleStringURIParameter("PARAmeter");
        urlParameter2 = new SingleBooleanURIParameter("bool");
        testCommand1 = new TURIActionCommand();
        testCommand2 = new TURIActionCommand();
        testHandler1 = new TURIPathSegmentActionMapper("abc", testCommand1);
        testHandler1.registerURLParameterForTest(urlParameter);
        testHandler1.registerURLParameterForTest(urlParameter2);
        testHandler2 = new TURIPathSegmentActionMapper("123", testCommand2);
        testHandler3 = new TURIPathSegmentActionMapper("cmd", testCommand1);
        dispatchingHandler = new org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper("test");
        dispatcher.addURIPathSegmentMapper(dispatchingHandler);
        dispatchingHandler.addSubMapper(testHandler1);
        dispatchingHandler.addSubMapper(testHandler2);
        dispatchingHandler.addSubMapper(testHandler3);

        caseSensitiveTestHandler1 = new TURIPathSegmentActionMapper("ABC", testCommand1);
        caseSensitiveTestHandler1.registerURLParameterForTest(urlParameter);
        caseSensitiveDispatchingHandler = new org.roklib.webapps.uridispatching.mapper.DispatchingURIPathSegmentActionMapper("TEST");
        caseSensitiveDispatcher.getRootActionMapper().addSubMapper(caseSensitiveDispatchingHandler);
        caseSensitiveDispatchingHandler.addSubMapper(caseSensitiveTestHandler1);
    }

    @Test
    public void test404CommandExecution() {
        TURIActionCommand test404ActionCommand = new TURIActionCommand();
        dispatcher.setDefaultAction(test404ActionCommand);
        dispatcher.handleURIAction("test/123");
        assertFalse(test404ActionCommand.executed);
        dispatcher.handleURIAction("no/actionhandler/registered");
        assertTrue(test404ActionCommand.executed);
    }
    
    @Test
    public void testCaseSensitiveActionHandling() {
        caseSensitiveDispatcher.handleURIAction("TEST/ABC");
        assertTrue(testCommand1.executed);
    }

    @Test
    public void testCaseSensitiveActionHandlingFails() {
        caseSensitiveDispatcher.handleURIAction("test/ABC");
        assertFalse(testCommand1.executed);
    }

    @Test
    public void testAddActionArgument() {
        assertEquals("/test/abc", testHandler1.getParameterizedActionURI(true).toString());
        testHandler1.addActionArgument("id", 1234);
        assertEquals("/test/abc?id=1234", testHandler1.getParameterizedActionURI(false).toString());
        testHandler1.addActionArgument("id", 9999);
        assertEquals("/test/abc?id=1234&id=9999", testHandler1.getParameterizedActionURI(true).toString());

        testHandler2.addActionArgument("v", 1, 2, 3);
        assertEquals("/test/123?v=1&v=2&v=3", testHandler2.getParameterizedActionURI(false).toString());
        testHandler2.addActionArgument("test", true);
        assertEquals("/test/123?v=1&v=2&v=3&test=true", testHandler2.getParameterizedActionURI(true).toString());

        // test DIRECTORY_WITH_NAMES parameter mode
        testHandler1.addActionArgument("id", 1234);
        testHandler1.addActionArgument("param", "value_a", "value_b");
        assertEquals("/test/abc/id/1234/param/value_a/param/value_b",
                testHandler1.getParameterizedActionURI(true, ParameterMode.DIRECTORY_WITH_NAMES).toString());

        // test DIRECTORY parameter mode (parameter names are not contained in URL)
        testHandler2.addActionArgument("id", 1234);
        testHandler2.addActionArgument("param", "value");
        assertEquals("/test/123/1234/value", testHandler2.getParameterizedActionURI(false, ParameterMode.DIRECTORY)
                .toString());

        // test getting hashbanged action URI
        assertEquals("#!test/123/1234/value",
                testHandler2.getParameterizedHashbangActionURI(true, ParameterMode.DIRECTORY).toString());

        // test that parameters appear in the order they were added in the URL
        testHandler1.clearActionArguments();
        testHandler1.addActionArgument("first", "1");
        testHandler1.addActionArgument("second", "2");
        testHandler1.addActionArgument("third", "3");
        assertEquals("/test/abc?first=1&second=2&third=3",
                testHandler1.getParameterizedActionURI(true, ParameterMode.QUERY).toString());
    }

    @Test
    public void testClearActionArguments() {
        testHandler1.addActionArgument("v", 1, 2, 3);
        testHandler1.addActionArgument("test", true);
        assertEquals("/test/abc?v=1&v=2&v=3&test=true", testHandler1.getParameterizedActionURI(true).toString());
        testHandler1.clearActionArguments();
        assertEquals("/test/abc", testHandler1.getParameterizedActionURI(true).toString());
    }

    @Test
    public void testGetActionName() {
        assertEquals("abc", testHandler1.getMapperName());
    }

    @Test
    public void testGetCaseInsensitiveActionName() {
        assertEquals("test", caseSensitiveDispatchingHandler.getCaseInsensitiveActionName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSubHandlerTwice() {
        testHandler1.addSubMapper(testHandler2);
    }

    @Test
    public void testGetActionURI() {
        assertEquals("/test/abc", testHandler1.getActionURI());
        assertEquals("/test/123", testHandler2.getActionURI());
        assertEquals("/test", dispatchingHandler.getActionURI());
    }
}
