package com.kong.konnect.consumer;

import com.kong.konnect.consumer.config.ConfigLoader;
import com.kong.konnect.consumer.config.KafkaConsumerConfig;
import com.kong.konnect.consumer.config.OpenSearchClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerService implements IConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final RestClient openSearchClient;
    private final ExecutorService executorService;
    private final String indexName;
    private boolean running = true;

    public ConsumerService() {
        Properties consumerProps = KafkaConsumerConfig.getConsumerConfig();
        this.kafkaConsumer = new KafkaConsumer<>(consumerProps);
        this.kafkaConsumer.subscribe(List.of(ConfigLoader.getProperty("kafka.topic")));
        this.indexName = ConfigLoader.getProperty("opensearch.index");

        this.openSearchClient = OpenSearchClientConfig.getClient();

        int cores = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(cores);
    }

    @Override
    public void startConsuming() {
        logger.info("Starting consumer with {} threads.", Runtime.getRuntime().availableProcessors());
        while (running) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                executorService.submit(() -> processRecord(record));
            }
        }
    }

    private void processRecord(ConsumerRecord<String, String> record) {
        String jsonData = record.value();
        logger.info("Processing record with key: {}, value: {}", record.key(), jsonData);

        try {
            Request request = new Request("POST", "/" + this.indexName + "/_doc");
            request.setJsonEntity(jsonData);
            Response response = openSearchClient.performRequest(request);
            logger.info("Data persisted to OpenSearch. Status: {}", response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            logger.error("Error persisting data to OpenSearch", e);
        }
    }

    @Override
    public void stopConsuming() {
        logger.info("Stopping consumer...");
        running = false;
        kafkaConsumer.close();
        executorService.shutdown();
        try {
            openSearchClient.close();
        } catch (IOException e) {
            logger.error("Error closing OpenSearch client", e);
        }
    }
}
