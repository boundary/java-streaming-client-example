package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

/**
 * A test which connects to the Boundary Streaming API for a known Conversation and receives 10 streaming updates.
 */
public class ConversationQueryTest extends AbstractStreamingTest {

    public static final String BOUNDARY_CLIENT_CONVERSATION_ID = "boundary.client.conversationId";

    @Test
    public void testConversationQuery() throws Exception {
        String conversationId = getRequiredProperty(BOUNDARY_CLIENT_CONVERSATION_ID);
        ConversationQuery conversationQuery = new ConversationQuery(conversationId,
                organizationId,
                Type.total,
                Resolution.second);
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        streamingClient.subscribe(conversationQuery, new SchemaMessageListener() {
            @Override
            public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                super.onMessage(clientSessionChannel, message);
                handleMessage(message, schema);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(20, TimeUnit.SECONDS);
        assertTrue("Didn't received 10 messages for conversation: " + conversationId, countDownLatch.getCount() == 0L);
    }

}
