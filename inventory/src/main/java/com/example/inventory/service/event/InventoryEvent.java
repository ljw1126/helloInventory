package com.example.inventory.service.event;

public sealed interface InventoryEvent
        permits InventoryDecreasedEvent, InventoryUpdatedEvent {
}
