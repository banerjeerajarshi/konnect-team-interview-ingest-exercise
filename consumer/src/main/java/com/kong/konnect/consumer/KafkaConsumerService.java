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

/**
 * kafka consumer service
 */
public class KafkaConsumerService implements IConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final RestClient openSearchClient;
    private final ExecutorService executorService;
    private final String indexName;
    private boolean running = true;

    /**
     * default constructor sets up variables
     */
    public KafkaConsumerService() {
        Properties consumerProps = KafkaConsumerConfig.getConsumerConfig();
        this.kafkaConsumer = new KafkaConsumer<>(consumerProps);
        this.kafkaConsumer.subscribe(List.of(ConfigLoader.getProperty("kafka.topic")));
        this.indexName = ConfigLoader.getProperty("opensearch.index");

        this.openSearchClient = OpenSearchClientConfig.getClient();

        // figure out the number of cores on the machine so that consumer can work most efficiently
        int cores = Runtime.getRuntime().availableProcessors();
        // multithreaded consumer uses a threadpool to use all cpu cores to process messages super fast
        this.executorService = Executors.newFixedThreadPool(cores);
    }

    /**
     * start consuming from kafka
     */
    @Override
    public void startConsuming() {
        logger.info("Starting consumer with {} threads.", Runtime.getRuntime().availableProcessors());
        while (running) { // keep the consumer running
            // poll records from kafka with 1000 millis max wait time
            ConsumerRecords<String, String> records = kafkaConsumer.poll(java.time.Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                // submit the polled record to the threadpool to dump to opensearch
                executorService.submit(() -> processRecord(record));
            }
        }
    }

    /**
     * dump a record from kafka to opensearch
     * @param record
     */
    private void processRecord(ConsumerRecord<String, String> record) {
        String jsonData = record.value();
        // record key may help in aggregation or other activity later. right now not much useful.
        logger.info("Processing record with key: {}, value: {}", record.key(), jsonData);

        try {
            // prep the opensearch rest client to send the request
            Request request = new Request("POST", "/" + this.indexName + "/_doc");
            request.setJsonEntity(jsonData);
            // persist the data to opensearch
            Response response = openSearchClient.performRequest(request);
            logger.info("Data persisted to OpenSearch. Status: {}", response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            logger.error("Error persisting data to OpenSearch", e);
        }
    }

    /**
     * stop the consumer
     */
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
