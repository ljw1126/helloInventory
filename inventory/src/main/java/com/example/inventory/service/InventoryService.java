package com.example.inventory.service;

import com.example.inventory.repository.InventoryJpaRepository;
import com.example.inventory.repository.entity.InventoryEntity;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.exception.InsufficientStockException;
import com.example.inventory.service.exception.InvalidDecreaseQuantityException;
import com.example.inventory.service.exception.InvalidStockException;
import com.example.inventory.service.exception.ItemNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryService(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return inventoryJpaRepository.findByItemId(itemId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getItemId(), entity.getStock());
    }

    public @NotNull Inventory decreaseByItemId(String itemId, Long quantity) {
        if (quantity < 0) {
            throw new InvalidDecreaseQuantityException();
        }

        final InventoryEntity inventoryEntity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if (quantity > inventoryEntity.getStock()) {
            throw new InsufficientStockException();
        }

        final Integer updateCount = inventoryJpaRepository.decreaseStock(itemId, quantity);
        if (updateCount == 0) {
            throw new ItemNotFoundException();
        }

        final InventoryEntity updatedEntity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        return mapToDomain(updatedEntity);
    }

    public @NotNull Inventory updateStock(@NotNull String itemId, @NotNull Long newStock) {
        if (newStock < 0) {
            throw new InvalidStockException();
        }

        final InventoryEntity inventoryEntity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        inventoryEntity.setStock(newStock);
        InventoryEntity updatedEntity = inventoryJpaRepository.save(inventoryEntity);
        return mapToDomain(updatedEntity);
    }
}
