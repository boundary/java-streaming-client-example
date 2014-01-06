package com.boundary.streaming.client.example;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

/**
 * A test which utilizes the FilterByMeter mechanism of the Boundary Streaming API
 */
public class FilterByMetersQueryTest extends AbstractStreamingTest {

    public static final String BOUNDARY_CLIENT_OBS_DOMAIN_IDS = "boundary.client.obsDomainIds";

    @Test
    public void testFilterByMeterQuery() throws Exception {
        String obsDomainIdsProp = getRequiredProperty(BOUNDARY_CLIENT_OBS_DOMAIN_IDS);
        List<String> obsDomainIds = Lists.newArrayList();
        for (String obsDomainId : Splitter.on(',').trimResults().split(obsDomainIdsProp)) {
            obsDomainIds.add(Integer.valueOf(obsDomainId).toString()); // Coerce to integer
        }
        if (obsDomainIds.isEmpty()) {
            throw new IllegalStateException(
                    String.format("No observation domain ids found in '%s'", BOUNDARY_CLIENT_OBS_DOMAIN_IDS));
        }
        FilterByMetersQuery defaultQuery = new FilterByMetersQuery(Type.portProtocol, Resolution.second, obsDomainIds);
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        streamingClient.subscribe(defaultQuery, new SchemaMessageListener() {
            @Override
            public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                super.onMessage(clientSessionChannel, message);
                handleMessage(message, schema);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(30, TimeUnit.SECONDS);
        assertTrue("Didn't receive 10 messages for meters: " + obsDomainIds, countDownLatch.getCount() == 0L);
    }

}
