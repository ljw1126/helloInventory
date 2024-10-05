package com.example.inventory.repository.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Import(InventoryRedisRepositoryImpl.class)
@Testcontainers
@DataRedisTest
class InventoryRedisRepositoryTest {

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    InventoryRedisRepository sut;

    @Autowired
    StringRedisTemplate redisTemplate;

    final String existingItemId = "1";
    final String nonExistingItemId = "2";
    final Long stock = 100L;

    @BeforeEach
    void setUp() {
        redisTemplate.opsForValue().set(sut.key(existingItemId), stock.toString());
    }

    @Nested
    class GetStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, null을 반환한다")
        @Test
        void test() {
            final Long result = sut.getStock(nonExistingItemId);

            assertThat(result).isNull();
        }

        @DisplayName("itemId를 갖는 inventory가 있따면, stock을 반환한다")
        @Test
        void test2() {
            final Long result = sut.getStock(existingItemId);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(stock);
        }
    }

    @Nested
    class DecreaseStock {
        final Long quantity = 10L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 차감하고 반환한다")
        @Test
        void test() {
            final Long result = sut.decreaseStock(nonExistingItemId, quantity);

            assertThat(result).isEqualTo(-quantity);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 차감하고 반환한다")
        @Test
        void test2() {
            final Long result = sut.decreaseStock(existingItemId, quantity);

            assertThat(result).isEqualTo(stock - quantity);
        }
    }

    @Nested
    class DeleteStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, false를 반환한다")
        @Test
        void test() {
            Boolean result = sut.deleteStock(nonExistingItemId);

            assertThat(result).isFalse();
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 삭제하고 true를 반환한다")
        @Test
        void test2() {
            Boolean result = sut.deleteStock(existingItemId);

            assertThat(result).isTrue();

            final Long totalSize = redisTemplate.getConnectionFactory().getConnection().serverCommands().dbSize();
            assertThat(totalSize).isZero();
        }
    }

    @Nested
    class SetStock {
        final Long newStock = 200L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 수정하고 반환한다")
        @Test
        void test() {
            final Long result = sut.setStock(nonExistingItemId, newStock);

            assertThat(result).isEqualTo(newStock);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 수정하고 반환한다")
        @Test
        void test2() {
            final Long result = sut.setStock(existingItemId, newStock);

            assertThat(result).isEqualTo(newStock);
        }
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
