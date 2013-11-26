package com.boundary.streaming.client.example;

import org.cometd.bayeux.Message;

import java.util.Map;

/**
 * AbstractStreamingTest - Shared methods for our example tests.
 */
public abstract class AbstractStreamingTest {

    protected final String URL = "wss://ws.boundary.com/streaming/";
    protected final int numberOfProcessors = Runtime.getRuntime().availableProcessors();

    protected void printObjectArray(String arrayName, Object[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println(arrayName + "[" + i + "]: " + array[i]);
        }
    }

    // Use schema to print out values
    protected void printValuesFromSchema(String arrayName, Object[] schema, Object[] values) {
        for (int i = 0; i < values.length; i++) {

            // Retrieve an array of values
            Object[] value = (Object[]) values[i];
            for (int x = 0; x < schema.length; x++) {
                System.out.println(arrayName + "[" + x + "]: " + value[x]);
            }
        }
    }

    protected void handleMessage(Message message, Object[] schema) {
        Map<String, Object> data = message.getDataAsMap();

        Long timestamp = (Long) data.get("timestamp");

        // Schema is only provided on initial state dump
        Object[] fetchedSchema = (Object[]) data.get("schema");
        if (fetchedSchema != null && schema == null) {
            schema = fetchedSchema;
        }

        Object[] inserts = (Object[]) data.get("insert");
        Object[] removes = (Object[]) data.get("remove");

        if (timestamp != null) System.out.println("timestamp: " + timestamp);

        if (schema != null) {
            printObjectArray("schema", schema);
            if (inserts != null) printValuesFromSchema("inserts", schema, inserts);
            if (removes != null) printValuesFromSchema("removes", schema, removes);
        }

    }
}
