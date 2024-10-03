package com.example.inventory.common.controller;

import com.example.inventory.controller.consts.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Nullable T data,
        @Nullable ApiErrorResponse error
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error(@NotNull ErrorCodes errorCodes) {
        final ApiErrorResponse errorResponse = new ApiErrorResponse(errorCodes.message, errorCodes.code);
        return new ApiResponse<>(null, errorResponse);
    }
}

