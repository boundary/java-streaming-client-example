java-streaming-client-example
=============================

This project is an example of how to use a Java Websocket client to connect to the Boundary Streaming API.

# Overview

The project contains 2 tests. 

* ConversationQueryTest - A test which connects to the Boundary Streaming API for a known Conversation and receives 10 streaming updates
* FilterByMetersQueryTest - A test which utilizes the FilterByMeter mechanism of the Boundary Streaming API

The project contains 2 tests. In order to run these tests you will need to provide your OrganizationID and API Key, you can find these variables at the top of each test.

private final String organizationId = ""; // REPLACE WITH YOUR ORGANIZATION ID
private final String apiKey = ""; // REPLACE WITH YOUR API KEY

Additionally for the ConversationQueryTest you will need to provide a known Conversation Id...

private final String conversationId = ""; // REPLACE WITH A CONVERSATION ID

... and for the FilterByMetersQueryTest you will need to provide a list of known Observation Domain IDs

// YOU WILL NEED TO REPLACE THIS LIST WITH A LIST OF OBSERVATION DOMAIN IDS THAT CORRESPOND TO THE METERED
// SERVERS YOU WISH TO AGGREGATE FLOW DATA FOR.
List<String> obsDomainIds = Arrays.asList(new String[]{"1", "2", "3", "4", "5"});

Running the Tests
============================

Once you have updated these classes you can run the tests by executing mvn test

