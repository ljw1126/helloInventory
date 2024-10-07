package com.example.inventory.repository;

import com.example.inventory.repository.kafka.InventoryDecreasedKafkaEvent;
import com.example.inventory.repository.kafka.InventoryKafkaEvent;
import com.example.inventory.repository.kafka.InventoryUpdatedKafkaEvent;
import com.example.inventory.service.event.InventoryDecreasedEvent;
import com.example.inventory.service.event.InventoryEvent;
import com.example.inventory.service.event.InventoryEventPublisher;
import com.example.inventory.service.event.InventoryUpdatedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventPublisherImpl implements InventoryEventPublisher {

    private final StreamBridge streamBridge;

    public InventoryEventPublisherImpl(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publish(InventoryEvent event) {
        final InventoryKafkaEvent inventoryKafkaEvent = createFromDomainEvent(event);
        final Message<InventoryKafkaEvent> message = MessageBuilder.withPayload(inventoryKafkaEvent)
                .setHeader(MESSAGE_KEY, inventoryKafkaEvent.itemId())
                .build();

        streamBridge.send("inventory-out-0", message);
    }

    private InventoryKafkaEvent createFromDomainEvent(InventoryEvent event) {
        if (event instanceof InventoryUpdatedEvent converted) {
            return InventoryUpdatedKafkaEvent.from(converted);
        }

        return InventoryDecreasedKafkaEvent.from((InventoryDecreasedEvent) event);
    }
}
