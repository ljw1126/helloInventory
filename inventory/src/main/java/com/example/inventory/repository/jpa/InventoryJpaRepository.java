package com.example.inventory.repository.jpa;

import com.example.inventory.repository.jpa.entity.InventoryEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {
    @NotNull Optional<InventoryEntity> findByItemId(@NotNull String itemId);

    @Modifying(clearAutomatically = true)
    @Query("update InventoryEntity  i set i.stock = i.stock - :quantity, i.updatedAt = current timestamp " +
            "where i.itemId = :itemId")
    @NotNull Integer decreaseStock(@NotNull String itemId, @NotNull Long quantity);
}
