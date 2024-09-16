package com.kong.konnect.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProducerServiceTest {

    private KafkaProducer<String, String> kafkaProducerMock;
    private ProducerService producerService;

    @BeforeEach
    public void setUp() {
        kafkaProducerMock = mock(KafkaProducer.class);
        producerService = new ProducerService(kafkaProducerMock);
    }

    @Test
    public void testSendMessage() {
        // Arrange
        String topic = "cdc-events";
        String key = "test-key";
        String value = "test-value";


        @SuppressWarnings("unchecked")
        Future<RecordMetadata> futureMock = mock(Future.class);
        when(kafkaProducerMock.send(any(ProducerRecord.class))).thenReturn(futureMock);


        producerService.sendMessage(topic, key, value);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaProducerMock, times(1)).send(recordCaptor.capture());  // Verify send was called once

        ProducerRecord<String, String> capturedRecord = recordCaptor.getValue();
        assertEquals(topic, capturedRecord.topic());
        assertEquals(key, capturedRecord.key());
        assertEquals(value, capturedRecord.value());
    }

    //@Test
    public void testSendMessageException() {
        String topic = "test-topic";
        String key = "test-key";
        String value = "test-value";

        when(kafkaProducerMock.send(any(ProducerRecord.class))).thenThrow(new RuntimeException("Kafka send failed"));

        try {
            producerService.sendMessage(topic, key, value);
        } catch (RuntimeException e) {
            assertEquals("Kafka send failed", e.getMessage());
        }
    }

    @Test
    public void testClose() {
        producerService.close();
        verify(kafkaProducerMock, times(1)).flush();
        verify(kafkaProducerMock, times(1)).close();
    }
}
