package com.kong.konnect.consumer.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;

/**
 * configures the opensearch rest client
 */
public class OpenSearchClientConfig {

    /**
     * returns a rest client handle for opensearch
     * @return an opensearch rest client handle
     */
    public static RestClient getClient() {
        return RestClient.builder(
                        new HttpHost(ConfigLoader.getProperty("opensearch.host"),
                                Integer.parseInt(ConfigLoader.getProperty("opensearch.port")),
                                "http"))
                .build();
    }
}
