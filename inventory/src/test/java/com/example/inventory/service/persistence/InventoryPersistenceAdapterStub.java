package com.example.inventory.service.persistence;

import com.example.inventory.service.domain.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryPersistenceAdapterStub implements InventoryPersistenceAdapter {
    private final List<Inventory> inventories = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    public void addInventory(@NotNull String itemId, @NotNull Long stock) {
        final Long id = idGenerator.getAndIncrement();
        final Inventory inventory = new Inventory(id, itemId, stock);
        inventories.add(inventory);
    }

    @Override
    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return internalFindByItemId(itemId)
                .orElse(null);
    }

    @Override
    public @Nullable Inventory decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        final Optional<Inventory> optionalInventory = internalFindByItemId(itemId);

        if (optionalInventory.isEmpty()) {
            return null;
        }

        final Inventory inventory = optionalInventory.get();
        inventory.setStock(inventory.getStock() - quantity);
        return inventory;
    }

    @Override
    public @NotNull Inventory save(Inventory inventory) {
        final Long targetId = inventory.getId();
        final Optional<Inventory> optionalInventory = inventories.stream()
                .filter(i -> i.getId() != null && i.getId().equals(targetId))
                .findFirst();

        Inventory result;
        if (optionalInventory.isPresent()) { // 이미 있는 경우
            result = optionalInventory.get();
            result.setStock(inventory.getStock());
            return result;
        }

        // 신규 추가
        final Long id = idGenerator.getAndIncrement();
        result = new Inventory(id, inventory.getItemId(), inventory.getStock());
        inventories.add(result);
        return result;
    }

    private @Nullable Optional<Inventory> internalFindByItemId(@NotNull String itemId) {
        return inventories.stream()
                .filter(entity -> entity.getItemId().equals(itemId))
                .findFirst();
    }
}
