package com.example.inventory.service.event;

import com.example.inventory.test.assertion.Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;

@SpringBootTest
class InventoryEventPublisherTest {

    @Autowired
    InventoryEventPublisher sut;

    @Autowired
    OutputDestination outputDestination;

    @Nested
    class InventoryDecreasedEventTest {
        final String itemId = "1";
        final Long quantity = 10L;
        final Long stock = 90L;


        @DisplayName("InventoryDecreasedEvent 객체를 publish 하면 메시지가 발행된다")
        @Test
        void test() throws JsonProcessingException {
            final InventoryEvent event = new InventoryDecreasedEvent(itemId, quantity, stock);

            sut.publish(event);

            final Message<byte[]> result = outputDestination.receive(1000, "inventory-out-0");
            Assertions.assertDecreasedEventEquals(result, itemId, quantity, stock);
        }
    }

    @Nested
    class InventoryUpdatedEventTest {
        final String itemId = "1";
        final Long stock = 200L;

        @DisplayName("InventoryUpdatedEvent 객체를 publish 하면 메시지가 발행된다")
        @Test
        void test() throws JsonProcessingException {
            final InventoryEvent event = new InventoryUpdatedEvent(itemId, stock);

            sut.publish(event);

            final Message<byte[]> result = outputDestination.receive(1000, "inventory-out-0");
            Assertions.assertUpdatedEventEquals(result, itemId, stock);
        }
    }
}
