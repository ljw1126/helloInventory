package com.example.inventory.test.assertion;

import com.example.inventory.controller.consts.ErrorCodes;
import com.example.inventory.service.event.InventoryEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

    public static void assertMvcErrorEquals(MvcResult mvcResult, ErrorCodes errorCodes) throws UnsupportedEncodingException, JsonProcessingException {
        final String content = mvcResult.getResponse().getContentAsString();
        final JsonNode responseBody = objectMapper.readTree(content);
        final JsonNode error = responseBody.get("error");

        assertThat(error).isNotNull();
        assertThat(error.get("code").asLong()).isEqualTo(errorCodes.code);
        assertThat(error.get("message").asText()).isEqualTo(errorCodes.message);
    }

    public static void asserMvcDataEquals(MvcResult mvcResult, Consumer<JsonNode> consumer) throws UnsupportedEncodingException, JsonProcessingException {
        final String content = mvcResult.getResponse().getContentAsString();
        final JsonNode responseBody = objectMapper.readTree(content);
        final JsonNode data = responseBody.get("data");

        assertThat(data).isNotNull();
        consumer.accept(data);
    }

    public static void assertDecreasedEventEquals(@NotNull Message<byte[]> result,
                                                  @NotNull String itemId,
                                                  @NotNull Long quantity,
                                                  @NotNull Long stock) throws JsonProcessingException {
        final String payload = new String(result.getPayload()); // json string
        final JsonNode jsonNode = objectMapper.readTree(payload);

        assertThat(jsonNode.get("type").asText()).isEqualTo("InventoryDecreased");
        assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
        assertThat(jsonNode.get("quantity").asLong()).isEqualTo(quantity);
        assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);

        final String messageKey = result.getHeaders().get(InventoryEventPublisher.MESSAGE_KEY, String.class);
        assertThat(messageKey).isEqualTo(itemId);

    }

    public static void assertUpdatedEventEquals(@NotNull Message<byte[]> result,
                                                @NotNull String itemId,
                                                @NotNull Long stock) throws JsonProcessingException {

        final String payload = new String(result.getPayload()); // json string
        final JsonNode jsonNode = objectMapper.readTree(payload);

        assertThat(jsonNode.get("type").asText()).isEqualTo("InventoryUpdated");
        assertThat(jsonNode.get("itemId").asText()).isEqualTo(itemId);
        assertThat(jsonNode.get("stock").asLong()).isEqualTo(stock);

        final String messageKey = result.getHeaders().get(InventoryEventPublisher.MESSAGE_KEY, String.class);
        assertThat(messageKey).isEqualTo(itemId);
    }
}
