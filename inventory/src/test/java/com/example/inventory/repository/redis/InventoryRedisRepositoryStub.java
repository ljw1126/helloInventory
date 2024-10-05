package com.example.inventory.repository.redis;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.HashMap;
import java.util.Map;

@NoRepositoryBean
public class InventoryRedisRepositoryStub implements InventoryRedisRepository {
    private final Map<String, Long> stockMap = new HashMap<>();

    public void addStock(@NotNull String itemId, @NotNull Long stock) {
        stockMap.put(key(itemId), stock);
    }

    @Override
    public @Nullable Long getStock(@NotNull String itemId) {
        return stockMap.get(key(itemId));
    }

    @Override
    public @NotNull Long decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        final Long stock = stockMap.getOrDefault(key(itemId), 0L);
        final Long nextStock = stock - quantity;
        stockMap.put(key(itemId), nextStock);

        return nextStock;
    }

    @Override
    public @NotNull Boolean deleteStock(@NotNull String itemId) {
        final Boolean hasKey = stockMap.containsKey(key(itemId));
        if (hasKey) {
            stockMap.remove(key(itemId));
        }

        return hasKey;
    }

    @Override
    public @NotNull Long setStock(@NotNull String itemId, @NotNull Long stock) {
        stockMap.put(key(itemId), stock);
        return stock;
    }
}
