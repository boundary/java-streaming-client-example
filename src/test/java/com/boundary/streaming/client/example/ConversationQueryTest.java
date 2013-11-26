package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

/**
 * A test which connects to the Boundary Streaming API for a known Conversation and receives 10 streaming updates.
 */
public class ConversationQueryTest extends AbstractStreamingTest {

    private final String conversationId = ""; // REPLACE WITH A CONVERSATION ID
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

    @Test
    public void testConversationQuery() throws Exception {

        ConversationQuery conversationQuery = new ConversationQuery(conversationId,
                organizationId,
                Query.Type.total,
                Query.Resolution.second);


        StreamingClient streamingClient = new StreamingClient(organizationId,
                apiKey,
                URL,
                wsFactory,
                scheduledThreadPoolExecutor);


        streamingClient.subscribe(conversationQuery, new ClientSessionChannel.MessageListener() {

            Object[] schema = null;

            @Override
            public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                handleMessage(message, schema);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(20, TimeUnit.SECONDS);
        assertTrue(countDownLatch.getCount() == 0L);
        streamingClient.disconnect();
    }
}
