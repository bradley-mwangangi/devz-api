package com.devz.api.exceptions;

import jakarta.validation.constraints.NotNull;

public class NotFoundException extends RuntimeException {

    public NotFoundException(@NotNull String message) {
        super(message);
    }
}
