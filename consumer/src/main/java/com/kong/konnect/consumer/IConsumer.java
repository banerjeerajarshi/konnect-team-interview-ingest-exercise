package com.kong.konnect.consumer;

/**
 * consumer interface that can be implemented by any specific consumer
 */
public interface IConsumer {
    void startConsuming();
    void stopConsuming();
}
