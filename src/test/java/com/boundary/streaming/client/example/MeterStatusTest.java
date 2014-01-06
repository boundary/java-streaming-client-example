package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

public class MeterStatusTest extends AbstractStreamingTest {

    @Test
    public void testMeterStatusQuery() throws Exception {
        Query meterStatusQuery = new Query() {
            @Override
            public String getQueryUrl() {
                return String.format("/query/%s/meter_status", organizationId);
            }
        };
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        streamingClient.subscribe(meterStatusQuery, new SchemaMessageListener() {
            @Override
            public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                super.onMessage(clientSessionChannel, message);
                handleMessage(message, schema);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(20, TimeUnit.SECONDS);
        assertTrue("Didn't receive a meter_status message", countDownLatch.getCount() == 0L);
    }

}
