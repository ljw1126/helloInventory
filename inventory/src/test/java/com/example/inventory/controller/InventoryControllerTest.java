package com.example.inventory.controller;

import com.example.inventory.config.JsonConfig;
import com.example.inventory.exception.NotImplementedException;
import com.example.inventory.service.InventoryService;
import com.example.inventory.service.domain.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/{itemId}", itemId))
                    .andExpectAll(status().isNotFound(),
                            jsonPath("$.error.code").value(404),
                            jsonPath("$.error.message").value("자산이 존재하지 않습니다"));
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() throws Exception {
            final Inventory inventory = new Inventory(itemId, stock);
            given(inventoryService.findByItemId(itemId))
                    .willReturn(inventory);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/inventory/{itemId}", itemId))
                    .andExpectAll(status().isOk(),
                            jsonPath("$.data.itemId").value(itemId),
                            jsonPath("$.data.stock").value(stock));
        }
    }

    @DisplayName("재고 차감")
    @Nested
    class DecreaseQuantity {
        @DisplayName("자산(상품)이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("재고가 부족할 경우, 400 status와 error를 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }

        @DisplayName("차감 수량이 음수일 경우, 400 status와 error를 반환한다")
        @Test
        void test3() {
            throw new NotImplementedException();
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() {
            throw new NotImplementedException();
        }
    }

    @DisplayName("재고 수정")
    @Nested
    class UpdateStock {
        @DisplayName("자산(상품)이 존재하지 않을 경우, 404 status와 error를 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("수정하려는 재고가 유효하지 않은 경우, 400 status와 error를 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }

        @DisplayName("정상인 경우, 200 status와 결과를 반환한다")
        @Test
        void success() {
            throw new NotImplementedException();
        }
    }
}
