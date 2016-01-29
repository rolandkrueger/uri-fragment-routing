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
