package com.kong.konnect.consumer.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;

public class OpenSearchClientConfig {

    public static RestClient getClient() {
        return RestClient.builder(
                        new HttpHost(ConfigLoader.getProperty("opensearch.host"),
                                Integer.parseInt(ConfigLoader.getProperty("opensearch.port")),
                                "http"))
                .build();
    }
}
