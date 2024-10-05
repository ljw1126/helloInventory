package com.example.inventory.repository;

import com.example.inventory.repository.entity.InventoryEntity;
import com.example.inventory.repository.jpa.InventoryJpaRepository;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.persistence.InventoryPersistenceAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class InventoryPersistenceAdapterImpl implements InventoryPersistenceAdapter {
    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryPersistenceAdapterImpl(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return inventoryJpaRepository.findByItemId(itemId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getId(), entity.getItemId(), entity.getStock());
    }

    @Override
    public @Nullable Inventory decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        final Integer updatedCount = inventoryJpaRepository.decreaseStock(itemId, quantity);
        if (updatedCount == 0) {
            return null;
        }

        return inventoryJpaRepository.findByItemId(itemId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    @Override
    public @NotNull Inventory save(Inventory inventory) {
        InventoryEntity entity = null;
        if (inventory.getId() != null) {
            entity = inventoryJpaRepository.findById(inventory.getId())
                    .orElse(null);
        }

        if (entity == null) { // id가 null이거나 db 에도 존재하지 않는 경우
            entity = new InventoryEntity(null, inventory.getItemId(), inventory.getStock());
        }

        entity.setStock(inventory.getStock());

        return mapToDomain(inventoryJpaRepository.save(entity));
    }
}
