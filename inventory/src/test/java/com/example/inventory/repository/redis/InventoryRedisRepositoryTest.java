package com.example.inventory.repository.redis;

import com.example.inventory.test.exception.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InventoryRedisRepositoryTest {
    @Nested
    class GetStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, null을 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 inventory가 있따면, stock을 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class DecreaseStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 차감하고 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 차감하고 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class DeleteStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, false를 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 삭제하고 true를 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class SetStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 수정하고 반환한다")
        @Test
        void test() {
            throw new NotImplementedException();
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 수정하고 반환한다")
        @Test
        void test2() {
            throw new NotImplementedException();
        }
    }
}
