package com.example.inventory.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static com.example.inventory.controller.consts.ErrorCodes.INSUFFICIENT_STOCK;
import static com.example.inventory.controller.consts.ErrorCodes.INVALID_STOCK;
import static com.example.inventory.controller.consts.ErrorCodes.ITEM_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@Transactional
@ActiveProfiles("integration-test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryIntegrationTest {

    private final String existingItemId = "1";
    private final String nonExistingItemId = "2";
    private final Long stock = 100L;

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("재고 조회 실패")
    @Test
    void test() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", nonExistingItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(ITEM_NOT_FOUND.code))
                .andExpect(jsonPath("$.error.message").value(ITEM_NOT_FOUND.message));
    }

    @DisplayName("재고 조회 성공")
    @Test
    void test2() throws Exception {
        successGetStock(existingItemId, stock);
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.opsForValue().set("inventory:" + existingItemId, stock.toString());
    }

    @DisplayName("재고 차감 실패")
    @Test
    void test3() throws Exception {
        //1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        //2. 재고 110개를 차감하고 실패한다.
        Long quantity = 110L;
        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(INSUFFICIENT_STOCK.code))
                .andExpect(jsonPath("$.error.message").value(INSUFFICIENT_STOCK.message));

        //3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 차감 성공")
    @Test
    void test4() throws Exception {
        //1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        //2. 재고 10개를 차감하고 성공한다.
        Long quantity = 10L;
        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemId").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(stock - quantity));

        //3. 재고를 조회하고 90개인 것을 확인한다.
        successGetStock(existingItemId, stock - quantity);

        //4. 재고 차감 이벤트 1번 발행된 것을 확인한다.
    }

    @DisplayName("재고 수정 실패")
    @Test
    void test5() throws Exception {
        //1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        //2. 재고를 -100개로 수정하고 실패한다.
        final Long newStock = -100L;
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("stock", newStock)))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(INVALID_STOCK.code))
                .andExpect(jsonPath("$.error.message").value(INVALID_STOCK.message));

        //3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 수정 성공")
    @Test
    void test6() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        //2. 재고를 1000개로 수정하고 성공한다.
        final Long newStock = 1000L;
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("stock", newStock)))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemId").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        //3. 재고를 조회하고 1000개인 것을 확인한다.
        successGetStock(existingItemId, newStock);
    }

    @DisplayName("재고 차감, 수정 종합")
    @Test
    void test7() throws Exception {
        //1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        //2. 재고 10개 차감을 7번 반복하고 성공한다.
        final Long quantity = 10L;
        for (int i = 1; i <= 7; i++) {
            mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                    ).andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.itemId").value(existingItemId))
                    .andExpect(jsonPath("$.data.stock").value(stock - (i * quantity)));
        }
        //3. 재고를 조회하고 30개인 것을 확인한다.
        successGetStock(existingItemId, stock - (7 * quantity));

        //4. 재고를 500개로 수정하고 성공한다.
        final Long newStock = 500L;
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("stock", newStock)))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemId").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        //5. 재고를 조회하고 500개인 것을 확인한다.
        successGetStock(existingItemId, newStock);
    }

    private void successGetStock(String itemId, Long stock) throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.itemId").value(itemId))
                .andExpect(jsonPath("$.data.stock").value(stock));
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
