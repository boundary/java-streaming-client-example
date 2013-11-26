package com.boundary.streaming.client.example;

/**
 * A @{link Query} which uses a known Conversation ID, @{link Type} and @{link Resolution} to connect
 * to the Boundary Streaming API.
 */
public class ConversationQuery implements Query {
    private final String conversationId;
    private final String organizationId;
    private final Type type;
    private final Resolution resolution;

    public ConversationQuery(String conversationId, String organizationId, Type type, Resolution resolution) {
        this.conversationId = conversationId;
        this.organizationId = organizationId;
        this.type = type;
        this.resolution = resolution;
    }

    @Override
    public String getQueryUrl() {
        if (type.getQueryParameter() != null) {
            return "/query/" + organizationId + "/fp-" + conversationId + "_volume_"
                    + resolution.getResolution() + "_" + type.getQueryParameter();
        }

        return "/query/" + organizationId + "/fp-" + conversationId + "_volume_"
                + resolution.getResolution();

    }
}