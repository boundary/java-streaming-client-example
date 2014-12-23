package com.boundary.streaming.client.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.cometd.websocket.client.JettyWebSocketTransport;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Example WebSockets based Boundary Java Streaming API Client
 */
public class StreamingClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingClient.class);
    private final String orgId;
    private final String apiKey;
    private final JettyWebSocketTransport transport;
    private final BayeuxClient client;
    private final ConcurrentMap<Query, List<MessageListener>> queryRefToListeners = Maps.newConcurrentMap();
    private final MessageListener subscriptionListener;

    public static final String SERVICE_QUERIES = "/service/queries";

    /**
     * Instantiate a new @{link StreamingClient}
     * @param orgId - Organization ID
     * @param apiKey - API Key
     * @param url - URL of the Streaming API
     * @param scheduler
     */
    public StreamingClient(final String orgId, String apiKey, String url,
                           ScheduledExecutorService scheduler) throws Exception {
        this.orgId = orgId;
        this.apiKey = apiKey;

        QueuedThreadPool wsThreadPool = new QueuedThreadPool();
        wsThreadPool.setName(wsThreadPool.getName() + "-client");
        wsThreadPool.start();
        SslContextFactory sslCtxFact = new SslContextFactory();
        WebSocketClient wsClient = new WebSocketClient(sslCtxFact, wsThreadPool);
        wsClient.start();

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(JettyWebSocketTransport.MAX_MESSAGE_SIZE_OPTION, 5 * 1024 * 1024);
        transport = new JettyWebSocketTransport(url, options, null, wsClient);
        client = new BayeuxClient(url, scheduler, transport);

        subscriptionListener = new MessageListener() {
            @Override
            public void onMessage(ClientSessionChannel channel, Message message) {
                LOGGER.info("Streaming client connected for: {}", orgId);
                for (Map.Entry<Query, List<MessageListener>> entry : queryRefToListeners.entrySet()) {
                    for (MessageListener listener : entry.getValue()) {
                        createSubscription(entry.getKey(), listener);
                    }
                }
            }
        };
        connect();
    }

    /**
     * Accepts a @{link Query} and @{link MessageListener} and creates a subscription for that query/listener pair.
     * @param query
     * @param listener
     */
    public void subscribe(Query query, MessageListener listener) {
        List<MessageListener> listeners = queryRefToListeners.putIfAbsent(query, Lists.newCopyOnWriteArrayList(ImmutableList.of(listener)));
        if (listeners != null) {
            listeners.add(listener);
        }

        // Subscribe now if possible, otherwise subscription will occur in handshake listener
        if (client.isConnected()) {
            createSubscription(query, listener);
        }
    }

    /**
     * Disconnects this @{link StreamingClient} from the API
     */
    public void disconnect() {
        client.getChannel(Channel.META_HANDSHAKE).unsubscribe(subscriptionListener);
        LOGGER.info("Waiting for client to disconnect for: {}", orgId);
        if (!client.disconnect(5000L)) {
            LOGGER.error("Client failed to disconnect cleanly");
        }
        transport.abort();
        LOGGER.info("Disconnected streaming client for: {}", orgId);
    }

    private void connect() {
        client.getChannel(Channel.META_HANDSHAKE).addListener(subscriptionListener);
        handshake(orgId, apiKey);
    }

    private void handshake(String orgId, String auth_token) {
        HashMap<String, Object> extOuter = new HashMap<String, Object>();
        HashMap<String, Object> ext = new HashMap<String, Object>();

        HashMap<String, String> authMapV2 = new HashMap<String, String>();
        authMapV2.put("org_id", orgId);
        authMapV2.put("token", auth_token);
        ext.put("authentication_v2", authMapV2);

        extOuter.put("ext", ext);

        client.handshake(extOuter);
    }

    /**
     * Creates a new subscription for this Query/Listener pair
     *
     * We need to handle FilterByMetersQuery a little differently, ideally this should be delegated
     * to the Query or another class, but for this example we're just doing it inline here with a instanceof
     * check.
     *
     * @param query
     * @param listener
     */
    private void createSubscription(final Query query, final MessageListener listener) {
        String channelName = query.getQueryUrl();
        ClientSessionChannel channel = client.getChannel(channelName);

        if (query instanceof FilterByMetersQuery) {
            // Add a handler to allow subscription to filter_by_meters queries
            final ClientSessionChannel serviceQueryChannel = client.getChannel(SERVICE_QUERIES);
            serviceQueryChannel.addListener(new ClientSessionChannel.MessageListener() {
                @Override
                public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
                    Map<String, Object> data = message.getDataAsMap();
                    if (data == null) {
                        throw new RuntimeException("Error creating query: " + query.getQueryUrl());
                    }

                    String location = (String) data.get("location");
                    ClientSessionChannel channel = client.getChannel("/query/" + orgId + "/" + location);
                    channel.subscribe(listener);
                    serviceQueryChannel.removeListener(this);
                }
            });
            channel.publish(((FilterByMetersQuery) query).getExtParams());
        } else {
            // This is for our ConversationQueryTest, all we need to do here is subscribe the listener to the channel
            // that is fetched from the query.getQueryUrl
            channel.subscribe(listener);
        }

        LOGGER.info("Subscription created for: {}", channelName);
    }

}
