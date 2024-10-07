package com.example.inventory.service.event;

public interface InventoryEventPublisher {
    void publish(InventoryEvent event);
}
