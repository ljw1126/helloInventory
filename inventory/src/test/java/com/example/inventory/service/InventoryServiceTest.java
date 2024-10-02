package com.example.inventory.service;

import com.example.inventory.exception.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {
    @Nested
    class FindByItemId {
        @DisplayName("itemId를 갖는 entity를 조회하지 못하면, null을 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, inventory를 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class DecreaseByItemId {
        @DisplayName("수량 quantity가 음수라면, 예외를 던진다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity가 없다면, 예외를 던진다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }

        @DisplayName("수량 quantity가 재고 stock 보다 크면, 예외를 던진다")
        @Test
        void test3() {
            throw new NotImplementedException();
        }

        @DisplayName("재고 차감될 entity가 없다면, 예외를 던진다")
        @Test
        void test4() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity가 조회되면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test5() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class UpdateStock {
        @DisplayName("수정할 tock이 유효하지 않다면, 예외를 던진다")
        @Test
        void test1() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, 예외를 던진다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity를 조회하면, stock을 수정하고 inventory를 반환한다")
        @Test
        void test3() {
            throw new NotImplementedException();
        }
    }
}
