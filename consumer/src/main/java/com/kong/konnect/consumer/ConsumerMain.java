package com.kong.konnect.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerMain {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMain.class);

    public static void main(String[] args) {
        IConsumer consumerService = new ConsumerService();

        logger.info("Starting the consumer service...");
        consumerService.startConsuming();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered. Stopping the consumer...");
            consumerService.stopConsuming();
        }));
    }
}
