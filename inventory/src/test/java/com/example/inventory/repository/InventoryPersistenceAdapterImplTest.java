package com.example.inventory.repository;

import com.example.inventory.repository.jpa.InventoryJpaRepositoryStub;
import com.example.inventory.repository.redis.InventoryRedisRepositoryStub;
import com.example.inventory.service.domain.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InventoryPersistenceAdapterImplTest {

    @InjectMocks
    InventoryPersistenceAdapterImpl sut;

    @Spy
    InventoryJpaRepositoryStub inventoryJpaRepositoryStub;

    @Spy
    InventoryRedisRepositoryStub inventoryRedisRepositoryStub;

    @Nested
    class FindByItemId { // query method
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
            inventoryRedisRepositoryStub.addStock(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null를 반환한다")
        @Test
        void test() {
            final Inventory result = sut.findByItemId(nonExistingItemId);

            assertThat(result).isNull();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, inventory를 반환한다")
        @Test
        void success() {
            final Inventory result = sut.findByItemId(existingItemId);

            assertThat(result).isNotNull();

            assertThat(result.getId()).isNotNull();
            assertThat(result.getItemId()).isEqualTo(existingItemId);
            assertThat(result.getStock()).isEqualTo(stock);
        }
    }

    @Nested
    class DecreaseStock { // query annotation
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
            inventoryRedisRepositoryStub.addStock(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null을 반환한다")
        @Test
        void test() {
            final Long quantity = 10L;

            final Inventory result = sut.decreaseStock(nonExistingItemId, quantity);

            assertThat(result).isNull();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, stock을 차감하고 inventory를 반환한다")
        @Test
        void success() {
            final Inventory result = sut.decreaseStock(existingItemId, 10L);

            assertThat(result).isNotNull();
            assertThat(result.getStock()).isEqualTo(90L);
        }
    }

    @Nested
    class Save {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
            inventoryRedisRepositoryStub.addStock(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, entity를 추가하고 추가된 inventory를 반환한다")
        @Test
        void test() {
            final Long newStock = 1234L;

            final Inventory inventory = new Inventory(null, nonExistingItemId, newStock);
            final Inventory result = sut.save(inventory);

            assertThat(result.getId()).isNotNull();
            assertThat(result.getItemId()).isEqualTo(nonExistingItemId);
            assertThat(result.getStock()).isEqualTo(newStock);
        }

        @DisplayName("itemId를 갖는 entity가 있다면, entity를 수정하고 수정된 inventory를 반환한다")
        @Test
        void success() {
            final Long newStock = 1234L;
            final Inventory inventory = sut.findByItemId(existingItemId);

            inventory.setStock(newStock);
            final Inventory result = sut.save(inventory);

            assertThat(result.getId()).isNotNull();
            assertThat(result.getItemId()).isEqualTo(existingItemId);
            assertThat(result.getStock()).isEqualTo(newStock);
        }
    }
}
