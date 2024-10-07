package com.example.inventory.repository.kafka;

public sealed interface InventoryKafkaEvent
        permits InventoryDecreasedKafkaEvent, InventoryUpdatedKafkaEvent {
    String itemId();
}
