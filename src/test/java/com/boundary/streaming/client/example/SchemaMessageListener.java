package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

import java.util.Map;

public class SchemaMessageListener implements ClientSessionChannel.MessageListener {
    protected Object[] schema;

    @Override
    public void onMessage(ClientSessionChannel channel, Message message) {
        // Schema is only provided on initial state dump
        final Map<String, Object> data = message.getDataAsMap();
        if (data.containsKey("schema")) {
            schema = (Object[]) data.get("schema");
        }
    }
}
