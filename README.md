java-streaming-client-example
=============================

This project is an example of how to use a Java Websocket client to connect to the Boundary Streaming API.

The project contains 3 tests.

* ConversationQueryTest - A test which connects to the Boundary Streaming API for a known Conversation and receives 10 streaming updates
* FilterByMetersQueryTest - A test which utilizes the FilterByMeter mechanism of the Boundary Streaming API
* MeterStatusTest - A test which connects to the Boundary Streaming API meter_status query.

The project contains 3 tests. In order to run these tests you will need to provide your OrganizationID and API Key via the system properties:

```
boundary.client.organizationId - Boundary Organization Id.
boundary.client.apiKey - Boundary API Key.
```

ConversationQueryTest requires a known Conversation Id:

```
boundary.client.conversationId - Conversation ID.
```

FilterByMetersQueryTest requires a comma-separated list of Observation Domain IDs:

```
boundary.client.obsDomainIds - Comma-separated list of obs domain ids (i.e. "1,2,3,4,5").
```

Running the Tests
============================

To run the tests, run this command:

```
mvn -Dboundary.client.organizationId=[your_org_id] \
    -Dboundary.client.apiKey=[your_api_key] \
    -Dboundary.client.conversationId=[conversation_id] \
    -Dboundary.client.obsDomainIds=[obs_domain_ids] \
    test
```

