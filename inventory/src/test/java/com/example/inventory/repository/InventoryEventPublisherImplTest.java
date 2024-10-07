package com.example.inventory.repository;

import com.example.inventory.test.exception.NotImplementedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class InventoryEventPublisherImplTest {
    @Nested
    class InventoryDecreasedEventTest {
        @DisplayName("InventoryDecreasedEvent 객체를 publish 하면 메시지가 발행된다")
        @Test
        void test() {
            throw new NotImplementedException();
        }
    }

    @Nested
    class InventoryUpdatedEventTest {
        @DisplayName("InventoryUpdatedEvent 객체를 publish 하면 메시지가 발행된다")
        @Test
        void test() {
            throw new NotImplementedException();
        }
    }
}
