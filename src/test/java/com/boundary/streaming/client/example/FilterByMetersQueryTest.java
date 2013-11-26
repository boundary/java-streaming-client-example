package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

/**
 * A test which utilizes the FilterByMeter mechanism of the Boundary Streaming API
 */
public class FilterByMetersQueryTest extends AbstractStreamingTest {

    private final String organizationId = ""; // REPLACE WITH YOUR ORGANIZATION ID
    private final String apiKey = ""; // REPLACE WITH YOUR API KEY

    private final ExecutorThreadPool wsThreadPool = new ExecutorThreadPool(numberOfProcessors - 1,
            numberOfProcessors - 1,
            Long.MAX_VALUE,
            TimeUnit.SECONDS);

    private final WebSocketClientFactory wsFactory = new WebSocketClientFactory(wsThreadPool);
    private final ScheduledExecutorService scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(numberOfProcessors - 1);

    private final CountDownLatch countDownLatch = new CountDownLatch(10);

    // YOU WILL NEED TO REPLACE THIS LIST WITH A LIST OF OBSERVATION DOMAIN IDS THAT CORRESPOND TO THE METERED
    // SERVERS YOU WISH TO AGGREGATE FLOW DATA FOR.
    List<String> obsDomainIds = Arrays.asList(new String[]{"1", "2", "3", "4", "5"});

    @Test
    public void testFilterByMeterQuery() throws Exception {
        FilterByMetersQuery defaultQuery = new FilterByMetersQuery(
                Query.Type.portProtocol,
                Query.Resolution.second,
                obsDomainIds);

        StreamingClient streamingClient = new StreamingClient(organizationId,
                apiKey,
                URL,
                wsFactory,
                scheduledThreadPoolExecutor);


        streamingClient.subscribe(defaultQuery, new ClientSessionChannel.MessageListener() {
            Object[] schema = null;

            @Override
            public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                handleMessage(message, schema);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(30, TimeUnit.SECONDS);
        assertTrue(countDownLatch.getCount() == 0L);
        streamingClient.disconnect();
    }


}
