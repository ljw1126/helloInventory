package com.example.inventory.test.fixture;

import com.example.inventory.service.domain.Inventory;
import org.jetbrains.annotations.Nullable;

public class InventoryFixture {

    public static Inventory sampleInventory(
            @Nullable String itemId,
            @Nullable Long stock
    ) {
        if (itemId == null) {
            itemId = "1";
        }

        if (stock == null) {
            stock = 100L;
        }

        return new Inventory(itemId, stock);
    }
}
