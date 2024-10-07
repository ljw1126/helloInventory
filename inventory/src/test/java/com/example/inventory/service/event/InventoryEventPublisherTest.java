package com.example.inventory.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InventoryEventPublisherTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

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
            final String payload = new String(result.getPayload()); // json string
            final JsonNode jsonNode = objectMapper.readTree(payload);

            assertThat(jsonNode.get("type").asText()).isEqualTo("InventoryDecreased");
            assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
            assertThat(jsonNode.get("quantity").asLong()).isEqualTo(quantity);
            assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);

            final String messageKey = result.getHeaders().get(InventoryEventPublisher.MESSAGE_KEY, String.class);
            assertThat(messageKey).isEqualTo(itemId);
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
            final String payload = new String(result.getPayload()); // json string
            final JsonNode jsonNode = objectMapper.readTree(payload);

            assertThat(jsonNode.get("type").asText()).isEqualTo("InventoryUpdated");
            assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
            assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);

            final String messageKey = result.getHeaders().get(InventoryEventPublisher.MESSAGE_KEY, String.class);
            assertThat(messageKey).isEqualTo(itemId);
        }
    }
}
