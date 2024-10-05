package com.example.inventory.service.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Inventory {
    private @Nullable Long id;
    private @NotNull String itemId;
    private @NotNull Long stock;

    public Inventory(@NotNull String itemId, @NotNull Long stock) {
        this(null, itemId, stock);
    }

    public Inventory(@Nullable Long id, @NotNull String itemId, @NotNull Long stock) {
        this.id = id;
        this.itemId = itemId;
        this.stock = stock;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(@NotNull Long stock) {
        this.stock = stock;
    }
}
