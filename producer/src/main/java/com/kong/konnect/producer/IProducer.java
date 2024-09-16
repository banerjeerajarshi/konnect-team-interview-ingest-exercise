package com.kong.konnect.producer;

public interface IProducer {
    void sendMessage(String topic, String key, String value);
    void close();
}
