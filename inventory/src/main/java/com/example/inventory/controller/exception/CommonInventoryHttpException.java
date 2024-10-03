package com.example.inventory.controller.exception;

import com.example.inventory.controller.consts.ErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class CommonInventoryHttpException extends RuntimeException {
    @NotNull
    private final ErrorCodes errorCodes;
    @NotNull
    private final HttpStatus status;

    public CommonInventoryHttpException(@NotNull ErrorCodes errorCodes, @NotNull HttpStatus status) {
        this.errorCodes = errorCodes;
        this.status = status;
    }

    public @NotNull ErrorCodes getErrorCodes() {
        return errorCodes;
    }

    public @NotNull HttpStatus getStatus() {
        return status;
    }
}
