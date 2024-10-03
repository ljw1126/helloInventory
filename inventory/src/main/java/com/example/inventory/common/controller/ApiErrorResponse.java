package com.example.inventory.common.controller;

import org.jetbrains.annotations.NotNull;

public record ApiErrorResponse(
        @NotNull String message,
        @NotNull Long code
) {

}
