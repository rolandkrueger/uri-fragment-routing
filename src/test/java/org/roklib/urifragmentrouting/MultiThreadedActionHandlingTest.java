package org.roklib.urifragmentrouting;

import org.junit.Test;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.RoutingContext;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the {@link UriActionMapperTree} in a multi-threaded scenario. One instance of the action mapper tree is shared
 * between several threads which use it to interpret parameterized URI fragments. Each thread interprets only URI
 * fragments which contain their respective thread ID as a URI fragment parameter.
 */
public class MultiThreadedActionHandlingTest {
    private final static Logger LOG = LoggerFactory.getLogger(MultiThreadedActionHandlingTest.class);

    private static UriActionMapperTree mapperTree;
    private static List<UriPathSegmentActionMapper> mappers;

    private static final String THREAD_ID = "threadId";

    static {
        mappers = new ArrayList<>();
        UriActionCommandFactory cmdFactory = ThreadedActionCommand::new;

        // @formatter:off
        mapperTree = UriActionMapperTree.create().buildMapperTree()
                .mapSubtree("sun").onSubtree()
                    .mapSubtree("earth").withSingleValuedParameter(THREAD_ID).forType(Long.class).noDefault()
                        .onSubtree().map("moon").onActionFactory(cmdFactory).finishMapper(mappers::add)
                    .finishMapper()
                    .mapSubtree("jupiter").withSingleValuedParameter(THREAD_ID).forType(Long.class).noDefault()
                        .onSubtree()
                           .map("europa").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("ganymede").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("callisto").onActionFactory(cmdFactory).finishMapper(mappers::add)
                    .finishMapper()
                    .mapSubtree("saturn").withSingleValuedParameter(THREAD_ID).forType(Long.class).noDefault()
                        .onSubtree()
                           .map("titan").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("dione").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("rhea").onActionFactory(cmdFactory).finishMapper(mappers::add)
                    .finishMapper()
                    .mapSubtree("pluto").withSingleValuedParameter(THREAD_ID).forType(Long.class).noDefault()
                        .onSubtree()
                           .map("charon").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("styx").onActionFactory(cmdFactory).finishMapper(mappers::add)
                    .finishMapper()
                    .mapSubtree("mars").withSingleValuedParameter(THREAD_ID).forType(Long.class).noDefault()
                        .onSubtree()
                           .map("phobos").onActionFactory(cmdFactory).finishMapper(mappers::add)
                           .map("deimos").onActionFactory(cmdFactory).finishMapper(mappers::add)
                    .finishMapper()
                .finishMapper()
                .build();
        // @formatter:on

        LOG.info("Testing with following mapper tree:");
        LOG.info("----------------------------------");
        mapperTree.getMapperOverview().forEach(LOG::info);
        LOG.info("----------------------------------");
    }

    @Test
    public void testThreadSafety() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Callable<Void> callable = () -> {
            String uriFragment = mapperTree.assembleUriFragment(
                    MultiThreadedActionHandlingTest.this.createParameterValues(Thread.currentThread().getId()),
                    mappers.get(new Random().nextInt(mappers.size())));
            mapperTree.interpretFragment(uriFragment, new ThreadRoutingContext(Thread.currentThread().getId()));
            return null;
        };

        List<Callable<Void>> tasks = Collections.nCopies(2000, callable);
        List<Future<Void>> futures = executorService.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }
    }

    private CapturedParameterValues createParameterValues(Long threadId) {
        CapturedParameterValues values = new CapturedParameterValues();
        values.setValueFor("earth", THREAD_ID, ParameterValue.forValue(threadId));
        values.setValueFor("jupiter", THREAD_ID, ParameterValue.forValue(threadId));
        values.setValueFor("saturn", THREAD_ID, ParameterValue.forValue(threadId));
        values.setValueFor("pluto", THREAD_ID, ParameterValue.forValue(threadId));
        values.setValueFor("mars", THREAD_ID, ParameterValue.forValue(threadId));
        return values;
    }

    public static class ThreadedActionCommand implements UriActionCommand {
        private CapturedParameterValues parameters;
        private ThreadRoutingContext context;

        @Override
        public void run() {
            if (parameters.hasValueFor("earth", THREAD_ID)) {
                assertThreadIdMatches("earth");
            }
        }

        private void assertThreadIdMatches(String celestialBody) {
            assertThat(context.getThreadId(), equalTo(parameters.getValueFor(celestialBody, THREAD_ID).getValue()));
        }

        @AllCapturedParameters
        public void setParameters(CapturedParameterValues parameters) {
            this.parameters = parameters;
        }

        @RoutingContext
        public void setRoutingContext(ThreadRoutingContext context) {
            this.context = context;
        }
    }

    private static class ThreadRoutingContext {
        private Long threadId;

        public ThreadRoutingContext(Long threadId) {
            this.threadId = threadId;
        }

        private Long getThreadId() {
            return threadId;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ThreadRoutingContext{");
            sb.append("threadId=").append(threadId);
            sb.append('}');
            return sb.toString();
        }
    }


}