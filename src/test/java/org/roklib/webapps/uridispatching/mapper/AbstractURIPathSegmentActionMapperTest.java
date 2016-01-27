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
import org.roklib.webapps.uridispatching.parameter.URIParameterError;
import org.roklib.webapps.uridispatching.parameter.SingleBooleanURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleIntegerURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

import java.util.HashMap;
import java.util.Map;

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
    public void testDirectoryModeParameterEvaluation() {
        dispatcher.handleURIAction("TEST/ABC/parameterValue/true", ParameterMode.DIRECTORY);
        assertEquals("parameterValue", urlParameter.getValue());
        assertEquals(true, urlParameter2.getValue());
    }

    @Test
    public void testDirectoryModeWithNamesParameterEvaluation() {
        dispatcher.handleURIAction("TEST/ABC/bool/true/PARAmeter/parameterValue", ParameterMode.DIRECTORY_WITH_NAMES);
        assertEquals("parameterValue", urlParameter.getValue());
        assertEquals(true, urlParameter2.getValue());
    }

    @Test
    public void testDirectoryModeWithMissingValuesParameterEvaluation() {
        dispatcher.handleURIAction("TEST/ABC/bool/true/", ParameterMode.DIRECTORY_WITH_NAMES);
        assertFalse(urlParameter.hasValue());
        assertEquals(true, urlParameter2.getValue());
    }

    @Test
    public void testCaseInsensitiveParameterHandling() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("parameter", new String[]{"parameterValue"});
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("TEST/ABC");
        assertEquals("parameterValue", urlParameter.getValue());
    }

    @Test
    public void testCaseSensitiveParameterHandling() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("PARAmeter", new String[]{"parameterValue"});
        caseSensitiveDispatcher.handleParameters(parameters);
        caseSensitiveDispatcher.handleURIAction("TEST/ABC");
        assertEquals("parameterValue", urlParameter.getValue());
    }

    @Test
    public void testCaseSensitiveParameterHandlingFail() {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("parameter", new String[]{"parameterValue"});
        caseSensitiveDispatcher.handleParameters(parameters);
        caseSensitiveDispatcher.handleURIAction("TEST/ABC");
        assertNotSame("parameterValue", urlParameter.getValue());
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
    public void testMandatoryParameters() {
        SingleStringURIParameter parameter1 = new SingleStringURIParameter("param");
        SingleStringURIParameter parameter2 = new SingleStringURIParameter("arg");
        testHandler3.registerURLParameterForTest(parameter1);
        testHandler3.registerURLParameterForTest(parameter2);
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("param", new String[]{"parameterValue"});

        // set only one parameter, but do not set the second, mandatory parameter
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertTrue(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter1.getError(), URIParameterError.NO_ERROR);
        assertEquals(parameter2.getError(), URIParameterError.PARAMETER_NOT_FOUND);

        // now also set the second parameter
        parameters.put("arg", new String[]{"argumentValue"});
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertFalse(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter1.getError(), URIParameterError.NO_ERROR);
        assertEquals(parameter2.getError(), URIParameterError.NO_ERROR);
    }

    @Test
    public void testOptionalParameters() {
        SingleStringURIParameter parameter1 = new SingleStringURIParameter("param");
        SingleStringURIParameter parameter2 = new SingleStringURIParameter("arg");
        testHandler3.registerURLParameterForTest(parameter1);
        testHandler3.registerURLParameterForTest(parameter2);
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("param", new String[]{"parameterValue"});

        // set only one parameter, but do not set the second, optional parameter
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertFalse(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter1.getError(), URIParameterError.NO_ERROR);
        assertEquals(parameter2.getError(), URIParameterError.NO_ERROR);
        assertTrue(parameter1.hasValue());
        assertFalse(parameter2.hasValue());
    }

    @Test
    public void testMandatoryParametersWithDefaultValue() {
        SingleStringURIParameter parameter1 = new SingleStringURIParameter("param");
        SingleStringURIParameter parameter2 = new SingleStringURIParameter("arg");
        parameter2.setOptional("DEFAULT");
        testHandler3.registerURLParameterForTest(parameter1);
        testHandler3.registerURLParameterForTest(parameter2);
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("param", new String[]{"parameterValue"});

        // set only one parameter, but do not set the second, mandatory parameter
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertFalse(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter1.getError(), URIParameterError.NO_ERROR);
        assertEquals(parameter2.getError(), URIParameterError.NO_ERROR);
        assertTrue(parameter1.hasValue());
        assertTrue(parameter2.hasValue());
        assertEquals(parameter1.getValue(), "parameterValue");
        // the second parameter contains the default value
        assertEquals(parameter2.getValue(), "DEFAULT");

        // now also set the second parameter
        parameters.put("arg", new String[]{"argumentValue"});
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertFalse(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter1.getValue(), "parameterValue");
        assertEquals(parameter2.getValue(), "argumentValue");
    }

    @Test
    public void testOptionalParameterWithConversionError() {
        SingleIntegerURIParameter parameter = new SingleIntegerURIParameter("int");
        testHandler3.registerURLParameterForTest(parameter);

        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put("int", new String[]{"one"});
        dispatcher.handleParameters(parameters);
        dispatcher.handleURIAction("test/cmd");
        assertTrue(testHandler3.haveRegisteredURIParametersErrors());
        assertEquals(parameter.getError(), URIParameterError.CONVERSION_ERROR);
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
