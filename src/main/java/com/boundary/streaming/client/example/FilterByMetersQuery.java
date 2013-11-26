package com.boundary.streaming.client.example;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * FilterByMeters Query
 *
 * A filterable query that uses a @{link Type}, @{link Resolution} and {@link List} of Observation Domain IDs
 * to as input for subscribing to the Boundary streaming APIs.
 */
public class FilterByMetersQuery implements Query {

    private final Map<String, Object> extParams = Maps.newConcurrentMap();
    private final Map<String, Object> extOuter = Maps.newConcurrentMap();
    private final String id = UUID.randomUUID().toString();

    public static final String FILTER_BY_METERS_URL = StreamingClient.SERVICE_QUERIES + "/filter_by_meters";

    public FilterByMetersQuery(Type type, Resolution resolution, List<String> observationDomainIds) {

        extParams.put("id", id);
        extParams.put("query", "volume_" + resolution.getResolution() + "_meter_" + type.getQueryParameter());

        // Provide a list of ObservationDomainIDs for this query.
        extParams.put("observation_domain_ids", observationDomainIds);
        extOuter.put("ext", extParams);
    }

    @Override
    public String getQueryUrl() {
        return FILTER_BY_METERS_URL;
    }


    public Map<String, Object> getExtParams() {
        return extOuter;
    }
}
