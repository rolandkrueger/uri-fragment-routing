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
