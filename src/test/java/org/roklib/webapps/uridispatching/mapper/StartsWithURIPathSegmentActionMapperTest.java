package org.roklib.webapps.uridispatching.mapper;

import org.junit.Before;
import org.junit.Test;
import org.roklib.webapps.uridispatching.TURIActionCommand;
import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.URIActionDispatcher;
import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

public class StartsWithURIPathSegmentActionMapperTest {
    private URIActionDispatcher dispatcher;
    private TURIPathSegmentActionMapper testActionHandler;
    private Class<? extends URIActionCommand> testActionCommand;
    private StartsWithURIPathSegmentActionMapper startsWithActionHandler;
    private Class<? extends URIActionCommand> startsWithActionCommand;
    private TURIPathSegmentActionMapper lastActionHandler;
    private Class<? extends URIActionCommand> lastActionCommand;

    @Before
    public void setUp() {
        dispatcher = new URIActionDispatcher();

        testActionCommand = TURIActionCommand.class;
        testActionHandler = new TURIPathSegmentActionMapper("testhandler", testActionCommand);

        startsWithActionHandler = new StartsWithURIPathSegmentActionMapper("test", new SingleStringURIParameter("value"));
        startsWithActionCommand = TURIActionCommand.class;
        startsWithActionHandler.setActionCommandClass(startsWithActionCommand);

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

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Fail() {
        new StartsWithURIPathSegmentActionMapper("  ", new SingleStringURIParameter("value"));
    }

    private void assertOutcome() {
        assertActionCommandWasExecuted(startsWithActionCommand);
        assertMatchedTokenFragments(startsWithActionHandler, new String[]{"value"});
    }

    private void assertActionCommandWasExecuted(Class<? extends URIActionCommand> command) {
        // FIXME
        //assertTrue(command.executed);
    }

    private void assertMatchedTokenFragments(RegexURIPathSegmentActionMapper handler, String[] expectedTokenFragments) {
    }
}
