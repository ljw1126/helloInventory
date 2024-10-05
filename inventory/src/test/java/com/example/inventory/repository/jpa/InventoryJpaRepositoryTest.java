package com.example.inventory.repository.jpa;

import com.example.inventory.config.JpaConfig;
import com.example.inventory.repository.jpa.entity.InventoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@ActiveProfiles("h2-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class InventoryJpaRepositoryTest {

    @Autowired
    InventoryJpaRepository sut;

    @Autowired
    TestEntityManager entityManager;

    @Nested
    class FindByItemId { // query method
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @DisplayName("itemId를 갖는 entity가 없다면, empty를 반환한다")
        @Test
        void test() {
            final Optional<InventoryEntity> result = sut.findByItemId(nonExistingItemId);

            assertThat(result).isEmpty();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, entity를 반환한다")
        @Test
        void success() {
            final Optional<InventoryEntity> result = sut.findByItemId(existingItemId);

            assertThat(result).isPresent();

            final InventoryEntity entity = result.get();

            assertThat(entity.getId()).isNotNull();
            assertThat(entity.getItemId()).isEqualTo(existingItemId);
            assertThat(entity.getStock()).isEqualTo(stock);
        }
    }

    @Nested
    class DecreaseStock { // query annotation
        final String existingItemId = "1";
        final String nonExistingItemId = "2";

        @DisplayName("itemId를 갖는 entity가 없다면, 0을 반환한다")
        @Test
        void test() {
            final Long quantity = 10L;

            final Integer result = sut.decreaseStock(nonExistingItemId, quantity);

            assertThat(result).isZero();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, 1을 반환한다")
        @Test
        void success() {
            InventoryEntity inventoryEntity = sut.findByItemId(existingItemId).get();
            final LocalDateTime lastUpdatedAt = inventoryEntity.getUpdatedAt();
            final Long quantity = 10L;

            final Integer result = sut.decreaseStock(existingItemId, quantity);
            entityManager.clear();

            assertThat(result).isOne();

            final InventoryEntity entity = sut.findByItemId(existingItemId).get();
            assertThat(entity.getStock()).isEqualTo(100 - quantity);
            assertThat(entity.getUpdatedAt()).isNotEqualTo(lastUpdatedAt);
        }
    }

    @Nested
    class Save { // default
        final String existingItemId = "1";
        final String nonExistingItemId = "2";

        @DisplayName("itemId를 갖는 entity가 없다면, entity를 추가하고 추가된 entity를 반환한다")
        @Test
        void test() {
            final Long newStock = 1234L;

            final InventoryEntity entity = new InventoryEntity(null, nonExistingItemId, newStock);

            InventoryEntity result = sut.save(entity);
            assertThat(result.getId()).isNotNull();
            assertThat(result.getItemId()).isEqualTo(nonExistingItemId);
            assertThat(result.getStock()).isEqualTo(newStock);

            assertThat(result.getCreatedAt()).isNotNull();
            assertThat(result.getUpdatedAt()).isNotNull();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, entity를 수정하고 수정된 entity를 반환한다")
        @Test
        void success() {
            final Long newStock = 1234L;
            final InventoryEntity entity = sut.findByItemId(existingItemId).get();
            final LocalDateTime lastCreatedAt = entity.getCreatedAt();
            final LocalDateTime lastUpdateAt = entity.getUpdatedAt();

            entity.setStock(newStock);
            final InventoryEntity result = sut.save(entity);
            entityManager.flush();

            assertThat(result.getId()).isNotNull();
            assertThat(result.getItemId()).isEqualTo(existingItemId);
            assertThat(result.getStock()).isEqualTo(newStock);

            assertThat(result.getCreatedAt()).isNotNull()
                    .isEqualTo(lastCreatedAt);
            assertThat(result.getUpdatedAt()).isNotNull()
                    .isNotEqualTo(lastUpdateAt);
        }
    }
}
