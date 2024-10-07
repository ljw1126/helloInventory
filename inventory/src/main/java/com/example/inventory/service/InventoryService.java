package com.example.inventory.service;

import com.example.inventory.repository.jpa.entity.InventoryEntity;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.event.InventoryDecreasedEvent;
import com.example.inventory.service.event.InventoryEventPublisher;
import com.example.inventory.service.event.InventoryUpdatedEvent;
import com.example.inventory.service.exception.InsufficientStockException;
import com.example.inventory.service.exception.InvalidDecreaseQuantityException;
import com.example.inventory.service.exception.InvalidStockException;
import com.example.inventory.service.exception.ItemNotFoundException;
import com.example.inventory.service.persistence.InventoryPersistenceAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class InventoryService {
    private final InventoryPersistenceAdapter inventoryPersistenceAdapter;
    private final InventoryEventPublisher inventoryEventPublisher;

    public InventoryService(InventoryPersistenceAdapter inventoryPersistenceAdapter, InventoryEventPublisher inventoryEventPublisher) {
        this.inventoryPersistenceAdapter = inventoryPersistenceAdapter;
        this.inventoryEventPublisher = inventoryEventPublisher;
    }

    @Transactional(readOnly = true)
    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return inventoryPersistenceAdapter.findByItemId(itemId);
    }

    @Deprecated
    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getItemId(), entity.getStock());
    }

    public @NotNull Inventory decreaseByItemId(String itemId, Long quantity) {
        if (quantity < 0) {
            throw new InvalidDecreaseQuantityException();
        }

        final Inventory inventory = inventoryPersistenceAdapter.findByItemId(itemId);
        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        if (quantity > inventory.getStock()) {
            throw new InsufficientStockException();
        }

        final Inventory updatedInventory = inventoryPersistenceAdapter.decreaseStock(itemId, quantity);
        if (updatedInventory == null) {
            throw new ItemNotFoundException();
        }

        final InventoryDecreasedEvent event = new InventoryDecreasedEvent(itemId, quantity, updatedInventory.getStock());
        try {
            inventoryEventPublisher.publish(event);
        } catch (Exception e) {
            // do nothing
        }
        return updatedInventory;
    }

    public @NotNull Inventory updateStock(@NotNull String itemId, @NotNull Long newStock) {
        if (newStock < 0) {
            throw new InvalidStockException();
        }

        final Inventory inventory = inventoryPersistenceAdapter.findByItemId(itemId);
        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        inventory.setStock(newStock);
        Inventory updatedInventory = inventoryPersistenceAdapter.save(inventory);

        final InventoryUpdatedEvent event = new InventoryUpdatedEvent(itemId, updatedInventory.getStock());
        try {
            inventoryEventPublisher.publish(event);
        } catch (Exception e) {
            // do nothing
        }
        return updatedInventory;
    }
}
