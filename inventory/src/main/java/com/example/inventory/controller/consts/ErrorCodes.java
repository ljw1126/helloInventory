package com.example.inventory.controller.consts;

import org.jetbrains.annotations.NotNull;

public enum ErrorCodes {
    ITEM_NOT_FOUND("자산이 존재하지 않습니다", 404L);

    public final @NotNull String message;
    public final @NotNull Long code;

    ErrorCodes(String message, @NotNull Long code) {
        this.message = message;
        this.code = code;
    }
}
