package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.TURIActionCommand;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.URIActionDispatcher;
import org.roklib.webapps.uridispatching.mapper.URIPathSegmentActionMapper.ParameterMode;
import org.roklib.webapps.uridispatching.parameter.Point2DURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleBooleanURIParameter;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

import static org.junit.Assert.*;

public class AbstractURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testHandler1;
    private TURIPathSegmentActionMapper testHandler2;
    private TURIPathSegmentActionMapper testHandler3;
    private DispatchingURIPathSegmentActionMapper dispatchingHandler;
    private Class<? extends URIActionCommand> testCommand1;
    private Class<? extends URIActionCommand> testCommand2;
    private SingleStringURIParameter urlParameter;
    private SingleBooleanURIParameter urlParameter2;
    private DispatchingURIPathSegmentActionMapper caseSensitiveDispatchingHandler;
    private URIActionDispatcher caseSensitiveDispatcher;
    private TURIPathSegmentActionMapper caseSensitiveTestHandler1;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher(false);
        caseSensitiveDispatcher = new URIActionDispatcher(true);

        urlParameter = new SingleStringURIParameter("PARAmeter");
        urlParameter2 = new SingleBooleanURIParameter("bool");
        testCommand1 = TURIActionCommand.class;
        testCommand2 = TURIActionCommand.class;
        testHandler1 = new TURIPathSegmentActionMapper("abc", testCommand1);
        testHandler1.registerURLParameterForTest(urlParameter);
        testHandler1.registerURLParameterForTest(urlParameter2);
        testHandler2 = new TURIPathSegmentActionMapper("123", testCommand2);
        testHandler3 = new TURIPathSegmentActionMapper("cmd", testCommand1);
        dispatchingHandler = new DispatchingURIPathSegmentActionMapper("test");
        dispatcher.addURIPathSegmentMapper(dispatchingHandler);
        dispatchingHandler.addSubMapper(testHandler1);
        dispatchingHandler.addSubMapper(testHandler2);
        dispatchingHandler.addSubMapper(testHandler3);

        caseSensitiveTestHandler1 = new TURIPathSegmentActionMapper("ABC", testCommand1);
        caseSensitiveTestHandler1.registerURLParameterForTest(urlParameter);
        caseSensitiveDispatchingHandler = new DispatchingURIPathSegmentActionMapper("TEST");
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

    @Test(expected = IllegalArgumentException.class)
    public void cannot_register_two_parameters_with_the_same_name() {
        Point2DURIParameter pointParameter1 = new Point2DURIParameter("point", "x1", "y1");
        Point2DURIParameter pointParameter2 = new Point2DURIParameter("point", "x2", "y2");
        dispatchingHandler.registerURIParameter(pointParameter1);
        dispatchingHandler.registerURIParameter(pointParameter2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void cannot_register_two_parameters_with_the_same_parameter_names() {
        Point2DURIParameter pointParameter1 = new Point2DURIParameter("point_A", "x", "y");
        Point2DURIParameter pointParameter2 = new Point2DURIParameter("point_B", "x", "y");
        dispatchingHandler.registerURIParameter(pointParameter1);
        dispatchingHandler.registerURIParameter(pointParameter2);
    }

}
