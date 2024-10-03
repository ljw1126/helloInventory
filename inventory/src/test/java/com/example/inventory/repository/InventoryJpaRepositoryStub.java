package com.example.inventory.repository;

import com.example.inventory.repository.entity.InventoryEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryJpaRepositoryStub implements InventoryJpaRepository {
    private final List<InventoryEntity> inventoryEntities = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public @NotNull Optional<InventoryEntity> findByItemId(@NotNull String itemId) {
        return inventoryEntities.stream()
                .filter(entity -> entity.getItemId().equals(itemId))
                .findFirst();
    }

    public void addInventoryEntity(@NotNull String itemId, @NotNull Long stock) {
        final Long id = idGenerator.getAndIncrement();
        inventoryEntities.add(new InventoryEntity(id, itemId, stock));
    }

    @Override
    public @NotNull Integer decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        Optional<InventoryEntity> optionalEntity = inventoryEntities.stream()
                .filter(entity -> entity.getItemId().equals(itemId))
                .findFirst();

        if (optionalEntity.isEmpty()) {
            return 0;
        }

        final InventoryEntity entity = optionalEntity.get();
        entity.setStock(entity.getStock() - quantity);
        return 1;
    }

    @Override
    public @NotNull InventoryEntity save(@NotNull InventoryEntity inventoryEntity) {
        final Long targetId = inventoryEntity.getId();
        final Optional<InventoryEntity> optionalInventoryEntity = inventoryEntities.stream()
                .filter(entity -> entity.getId() != null && entity.getId().equals(targetId))
                .findFirst();

        InventoryEntity entity;
        if (optionalInventoryEntity.isPresent()) {
            entity = optionalInventoryEntity.get();
            entity.setStock(inventoryEntity.getStock());
            return entity;
        }

        final Long id = idGenerator.getAndIncrement();
        entity = new InventoryEntity(id, inventoryEntity.getItemId(), inventoryEntity.getStock());
        inventoryEntities.add(entity);
        return entity;
    }
}
