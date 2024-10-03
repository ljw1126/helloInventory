package com.example.inventory.repository;

import com.example.inventory.test.exception.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InventoryJpaRepositoryTest {

    @Nested
    class FindByItemId { // query method
        @DisplayName("itemId를 갖는 entity가 없다면, empty를 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, entity를 반환한다")
        @Test
        void success() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class DecreaseStock { // query annotation
        @DisplayName("itemId를 갖는 entity가 없다면, 0을 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, 1을 반환한다")
        @Test
        void success() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class Save { // default
        @DisplayName("itemId를 갖는 entity가 없다면, entity를 추가하고 추가된 entity를 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 entity가 있다면, entity를 수정하고 수정된 entity를 반환한다")
        @Test
        void success() {
            throw new NotImplementedException();
        }
    }
}
