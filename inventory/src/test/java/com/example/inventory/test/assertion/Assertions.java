package com.example.inventory.test.assertion;

import com.example.inventory.controller.consts.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
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
}
