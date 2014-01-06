package com.boundary.streaming.client.example;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.cometd.bayeux.Message;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AbstractStreamingTest - Shared methods for our example tests.
 */
public abstract class AbstractStreamingTest {

    public static final String BOUNDARY_CLIENT_API_KEY = "boundary.client.apiKey";
    public static final String BOUNDARY_CLIENT_ORGANIZATION_ID = "boundary.client.organizationId";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStreamingTest.class);
    protected final String URL = "wss://ws.boundary.com/streaming/";
    protected String organizationId;
    protected String apiKey;
    protected final int numberOfProcessors = Runtime.getRuntime().availableProcessors();

    private ExecutorThreadPool wsThreadPool;
    private WebSocketClientFactory wsFactory;
    private ScheduledExecutorService scheduledThreadPoolExecutor;
    protected StreamingClient streamingClient;

    @Before
    public void setup() {
        this.organizationId = getRequiredProperty(BOUNDARY_CLIENT_ORGANIZATION_ID);
        this.apiKey = getRequiredProperty(BOUNDARY_CLIENT_API_KEY);
        this.wsThreadPool = new ExecutorThreadPool(numberOfProcessors,
                numberOfProcessors,
                Long.MAX_VALUE,
                TimeUnit.SECONDS);
        this.wsFactory = new WebSocketClientFactory(wsThreadPool);
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(numberOfProcessors);
        this.streamingClient = new StreamingClient(organizationId,
                apiKey,
                URL,
                wsFactory,
                scheduledThreadPoolExecutor);
    }

    @After
    public void teardown() throws Exception {
        if (this.streamingClient != null) {
            this.streamingClient.disconnect();
        }
        if (this.scheduledThreadPoolExecutor != null) {
            this.scheduledThreadPoolExecutor.shutdown();
            this.scheduledThreadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        if (this.wsFactory != null) {
            this.wsFactory.stop();
        }
        if (this.wsThreadPool != null) {
            this.wsThreadPool.stop();
            this.wsThreadPool.join();
        }
    }

    public String getRequiredProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        if (Strings.isNullOrEmpty(value)) {
            throw new IllegalStateException(String.format("Required property '%s' not set.", propertyName));
        }
        return value;
    }

    // Use schema to print out values
    protected void printValuesFromSchema(String arrayName, Object[] schema, Object[] values) {
        int recordNum = 0;
        for (Object value1 : values) {
            // Retrieve an array of values
            final Object[] value = (Object[]) value1;
            final Map<String, Object> row = Maps.newLinkedHashMap();
            for (int x = 0; x < schema.length; x++) {
                row.put(schema[x].toString(), value[x]);
            }
            LOGGER.info("{}[{}]: {}", arrayName, recordNum++, row);
        }
    }

    protected void handleMessage(Message message, Object[] schema) {
        Map<String, Object> data = message.getDataAsMap();
        Object[] inserts = (Object[]) data.get("insert");
        Object[] removes = (Object[]) data.get("remove");

        // Schema is only provided on initial state dump
        if (data.containsKey("schema")) {
            schema = (Object[]) data.get("schema");
        }
        if (data.containsKey("timestamp")) {
            LOGGER.info("timestamp: {}", data.get("timestamp"));
        }
        if (schema != null) {
            LOGGER.info("schema: {}", Arrays.toString(schema));
            if (inserts != null) {
                printValuesFromSchema("inserts", schema, inserts);
            }
            if (removes != null) {
                printValuesFromSchema("removes", schema, removes);
            }
        }

    }
}
