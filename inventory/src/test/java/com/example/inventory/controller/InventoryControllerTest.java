package com.example.inventory.controller;

import com.example.inventory.config.JsonConfig;
import com.example.inventory.controller.consts.ErrorCodes;
import com.example.inventory.service.InventoryService;
import com.example.inventory.service.domain.Inventory;
import com.example.inventory.service.exception.InsufficientStockException;
import com.example.inventory.service.exception.InvalidDecreaseQuantityException;
import com.example.inventory.service.exception.InvalidStockException;
import com.example.inventory.service.exception.ItemNotFoundException;
import com.example.inventory.test.assertion.Assertions;
import com.example.inventory.test.fixture.InventoryFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(JsonConfig.class)
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    InventoryService inventoryService;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("재고 조회")
    @Nested
    class GetStock {
        final String itemId = "1";
        final Long stock = 100L;

        @DisplayName("자산(상품)이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test() throws Exception {
            given(inventoryService.findByItemId(itemId))
                    .willReturn(null);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/{itemId}", itemId))
                    .andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.ITEM_NOT_FOUND);
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() throws Exception {
            final Inventory inventory = InventoryFixture.sampleInventory(itemId, stock);
            given(inventoryService.findByItemId(itemId))
                    .willReturn(inventory);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/{itemId}", itemId))
                    .andExpect(status().isOk())
                    .andReturn();

            Assertions.asserMvcDataEquals(mvcResult, jsonNode -> {
                assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
                assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);
            });
        }
    }

    @DisplayName("재고 차감")
    @Nested
    class DecreaseQuantity {
        final String itemId = "1";
        final Long quantity = 10L;
        final Long stock = 90L;

        @DisplayName("자산(상품)이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test() throws Exception {
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(ItemNotFoundException.class);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/{itemId}/decrease", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                    ).andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.ITEM_NOT_FOUND);
        }

        @DisplayName("재고가 부족할 경우, 400 status와 error를 반환한다")
        @Test
        void test2() throws Exception {
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(InsufficientStockException.class);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/{itemId}/decrease", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                    ).andExpect(status().isBadRequest())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.INSUFFICIENT_STOCK);
        }

        @DisplayName("차감 수량이 음수일 경우, 400 status와 error를 반환한다")
        @Test
        void test3() throws Exception {
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willThrow(InvalidDecreaseQuantityException.class);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/{itemId}/decrease", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                    ).andExpect(status().isBadRequest())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.INVALID_DECREASE_QUANTITY);
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() throws Exception {
            final Inventory inventory = InventoryFixture.sampleInventory(itemId, stock);
            given(inventoryService.decreaseByItemId(itemId, quantity))
                    .willReturn(inventory);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/inventory/{itemId}/decrease", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("quantity", quantity)))
                    ).andExpect(status().isOk())
                    .andReturn();

            Assertions.asserMvcDataEquals(mvcResult, jsonNode -> {
                assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
                assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);
            });
        }
    }

    @DisplayName("재고 수정")
    @Nested
    class UpdateStock {
        final String itemId = "1";
        final Long stock = 100L;

        @DisplayName("자산(상품)이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test() throws Exception {
            given(inventoryService.updateStock(itemId, stock))
                    .willThrow(ItemNotFoundException.class);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/inventory/{itemId}/stock", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("stock", stock)))
                    ).andExpect(status().isNotFound())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.ITEM_NOT_FOUND);
        }

        @DisplayName("수정하려는 재고가 유효하지 않은 경우, 400 status와 error를 반환한다")
        @Test
        void test2() throws Exception {
            given(inventoryService.updateStock(itemId, stock))
                    .willThrow(InvalidStockException.class);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/inventory/{itemId}/stock", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("stock", stock)))
                    ).andExpect(status().isBadRequest())
                    .andReturn();

            Assertions.assertMvcErrorEquals(mvcResult, ErrorCodes.INVALID_STOCK);
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() throws Exception {
            final Inventory inventory = InventoryFixture.sampleInventory(itemId, stock);
            given(inventoryService.updateStock(itemId, stock))
                    .willReturn(inventory);

            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/inventory/{itemId}/stock", itemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("stock", stock)))
                    ).andExpect(status().isOk())
                    .andReturn();

            Assertions.asserMvcDataEquals(mvcResult, jsonNode -> {
                assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
                assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);
            });
        }
    }
}
