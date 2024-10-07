package com.example.inventory.service.event;

import org.jetbrains.annotations.NotNull;

public record InventoryUpdatedEvent(
        @NotNull String itemId,
        @NotNull Long stock
) implements InventoryEvent {
}
