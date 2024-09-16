package com.kong.konnect.producer;



import com.kong.konnect.producer.config.KafkaProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerService implements IProducer {

    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);
    private final KafkaProducer<String, String> producer;

    // Default constructor that uses KafkaProducerConfig to create a producer
    public ProducerService() {
        this.producer = KafkaProducerConfig.createProducer();
    }

    // Parameterized constructor for testing
    public ProducerService(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void sendMessage(String topic, String key, String value) {
        try {
            // Sending the message to the Kafka topic
            producer.send(new ProducerRecord<>(topic, key, value));
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka", e);
            throw new RuntimeException("Kafka send failed", e);
        }
    }

    @Override
    public void close() {
        try {
            producer.flush();
            producer.close();
            logger.info("Kafka producer closed successfully.");
        } catch (Exception e) {
            logger.error("Error closing Kafka producer", e);
        }
    }
}



