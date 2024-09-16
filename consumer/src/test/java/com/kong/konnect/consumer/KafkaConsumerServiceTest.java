package com.kong.konnect.consumer;


import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * mockito based unit tests
 */
class KafkaConsumerServiceTest {

    @Test
    void testStartConsuming() {
        IConsumer consumer = mock(IConsumer.class);
        consumer.startConsuming();
        verify(consumer).startConsuming();
    }

    @Test
    void testStopConsuming() {
        IConsumer consumer = mock(IConsumer.class);
        consumer.stopConsuming();
        verify(consumer).stopConsuming();
    }
}

