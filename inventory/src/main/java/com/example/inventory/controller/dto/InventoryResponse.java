package com.example.inventory.controller.dto;

import com.example.inventory.service.domain.Inventory;
import org.jetbrains.annotations.NotNull;

public record InventoryResponse(
        @NotNull String itemId,
        @NotNull Long stock) {

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(inventory.getItemId(), inventory.getStock());
    }
}
