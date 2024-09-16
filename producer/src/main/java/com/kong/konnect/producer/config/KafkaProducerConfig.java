package com.kong.konnect.producer.config;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaProducerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);
    public static KafkaProducer<String, String> createProducer() {
        String activeProfile = System.getProperty("env", "dev");
        String propertiesFile = String.format("application-%s.properties", activeProfile);
        Properties props = new Properties();

        try (InputStream input = ProducerConfig.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null) {
                logger.error("Sorry, unable to find {}", propertiesFile);
                return null;
            }

            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getProperty("kafka.bootstrap.servers"));
        props.put(ProducerConfig.ACKS_CONFIG, props.getProperty("kafka.acks"));
        props.put(ProducerConfig.RETRIES_CONFIG, props.getProperty("kafka.retries"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, props.getProperty("kafka.batch.size"));
        props.put(ProducerConfig.LINGER_MS_CONFIG, props.getProperty("kafka.linger.ms"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, props.getProperty("kafka.buffer.memory"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}

