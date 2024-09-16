package com.kong.konnect.producer;

/**
 * producer interface that can be implemented by any specific producer
 */
public interface IProducer {
    void sendMessage(String topic, String key, String value);
    void close();
}
