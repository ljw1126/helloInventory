package com.example.inventory.repository.kafka;

import com.example.inventory.service.event.InventoryUpdatedEvent;
import org.jetbrains.annotations.NotNull;

public record InventoryUpdatedKafkaEvent(
        @NotNull InventoryEventKafkaType type,
        @NotNull String itemId,
        @NotNull Long stock
) implements InventoryKafkaEvent {
    public static InventoryUpdatedKafkaEvent from(InventoryUpdatedEvent event) {
        return new InventoryUpdatedKafkaEvent(
                InventoryEventKafkaType.InventoryUpdated,
                event.itemId(),
                event.stock()
        );
    }
}
