package com.example.inventory.service.domain;

import org.jetbrains.annotations.NotNull;

public class Inventory {
    private @NotNull String itemId;
    private @NotNull Long stock;

    public Inventory(@NotNull String itemId, @NotNull Long stock) {
        this.itemId = itemId;
        this.stock = stock;
    }

    public String getItemId() {
        return itemId;
    }

    public Long getStock() {
        return stock;
    }
}
