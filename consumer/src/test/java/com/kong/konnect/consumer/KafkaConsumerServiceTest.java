package com.kong.konnect.consumer;


import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * mockito based unit tests
 */
class KafkaConsumerServiceTest {

    /**
     * test startconsuming method
     */
    @Test
    void testStartConsuming() {
        IConsumer consumer = mock(IConsumer.class);
        consumer.startConsuming();
        verify(consumer).startConsuming();
    }

    /**
     * test stopconsuming method
     */
    @Test
    void testStopConsuming() {
        IConsumer consumer = mock(IConsumer.class);
        consumer.stopConsuming();
        verify(consumer).stopConsuming();
    }
}

