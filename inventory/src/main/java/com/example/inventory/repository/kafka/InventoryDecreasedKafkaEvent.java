package com.example.inventory.repository.kafka;

import com.example.inventory.service.event.InventoryDecreasedEvent;
import org.jetbrains.annotations.NotNull;

public record InventoryDecreasedKafkaEvent(
        @NotNull InventoryEventKafkaType type,
        @NotNull String itemId,
        @NotNull Long quantity,
        @NotNull Long stock
) implements InventoryKafkaEvent {

    public static InventoryDecreasedKafkaEvent from(InventoryDecreasedEvent event) {
        return new InventoryDecreasedKafkaEvent(
                InventoryEventKafkaType.InventoryDecreased,
                event.itemId(),
                event.quantity(),
                event.stock()
        );
    }
}
