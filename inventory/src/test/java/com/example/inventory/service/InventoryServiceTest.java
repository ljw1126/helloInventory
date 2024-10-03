package com.example.inventory.service;

import com.example.inventory.repository.InventoryJpaRepositoryStub;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.exception.InsufficientStockException;
import com.example.inventory.service.exception.InvalidDecreaseQuantityException;
import com.example.inventory.service.exception.InvalidStockException;
import com.example.inventory.service.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @InjectMocks
    InventoryService sut;

    @Spy
    InventoryJpaRepositoryStub inventoryJpaRepository;

    @Nested
    class FindByItemId {
        final String existingItemId = "1";
        final Long stock = 10L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepository.addInventoryEntity(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity를 조회하지 못하면, null을 반환한다")
        @Test
        void test() {
            final String nonExistingItemId = "2";

            final Inventory inventory = sut.findByItemId(nonExistingItemId);

            assertThat(inventory).isNull();
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, inventory를 반환한다")
        @Test
        void test2() {
            final String existingItemId = "1";
            final Long stock = 10L;

            final Inventory inventory = sut.findByItemId(existingItemId);

            assertThat(inventory).isNotNull()
                    .extracting("itemId", "stock")
                    .containsExactly(existingItemId, stock);
        }
    }

    @Nested
    class DecreaseByItemId {
        final String existingItemId = "1";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepository.addInventoryEntity(existingItemId, stock);
        }

        @DisplayName("수량 quantity가 음수라면, 예외를 던진다")
        @Test
        void test() {
            final Long quantity = -1L;

            assertThatThrownBy(() -> {
                sut.decreaseByItemId(existingItemId, quantity);
            }).isInstanceOf(InvalidDecreaseQuantityException.class);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, 예외를 던진다")
        @Test
        void test2() {
            final Long quantity = 10L;
            final String nonExistingItemId = "2";

            assertThatThrownBy(() -> {
                sut.decreaseByItemId(nonExistingItemId, quantity);
            }).isInstanceOf(ItemNotFoundException.class);
        }

        @DisplayName("수량 quantity가 재고 stock 보다 크면, 예외를 던진다")
        @Test
        void test3() {
            final Long quantity = stock + 1;

            assertThatThrownBy(() -> {
                sut.decreaseByItemId(existingItemId, quantity);
            }).isInstanceOf(InsufficientStockException.class);
        }

        @DisplayName("변경될(재고차감) entity가 없다면, 예외를 던진다")
        @Test
        void test4() {
            final Long quantity = 10L;

            doReturn(0).when(inventoryJpaRepository)
                    .decreaseStock(existingItemId, quantity);

            assertThatThrownBy(() -> {
                sut.decreaseByItemId(existingItemId, quantity);
            }).isInstanceOf(ItemNotFoundException.class);

            verify(inventoryJpaRepository, times(1)).decreaseStock(existingItemId, quantity);
        }

        @DisplayName("itemId를 갖는 entity가 조회되면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test5() {
            final Long quantity = 10L;

            final Inventory result = sut.decreaseByItemId(existingItemId, quantity);

            assertThat(result).isNotNull()
                    .extracting("itemId", "stock")
                    .containsExactly(existingItemId, (stock - quantity));
        }
    }

    @Nested
    class UpdateStock {
        final String existingItemId = "1";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            inventoryJpaRepository.addInventoryEntity(existingItemId, stock);
        }

        @DisplayName("수정할 stock이 유효하지 않다면, 예외를 던진다")
        @Test
        void test1() {
            final Long newStock = -1L;

            assertThatThrownBy(() -> {
                sut.updateStock(existingItemId, newStock);
            }).isInstanceOf(InvalidStockException.class);
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, 예외를 던진다")
        @Test
        void test2() {
            final Long newStock = 200L;
            final String nonExistingItemId = "2";

            assertThatThrownBy(() -> {
                sut.updateStock(nonExistingItemId, newStock);
            }).isInstanceOf(ItemNotFoundException.class);
        }

        @DisplayName("itemId를 갖는 entity를 조회하면, stock을 수정하고 inventory를 반환한다")
        @Test
        void test3() {
            final Long newStock = 200L;

            final Inventory result = sut.updateStock(existingItemId, newStock);

            assertThat(result).isNotNull()
                    .extracting("itemId", "stock")
                    .containsExactly(existingItemId, newStock);
        }
    }
}
