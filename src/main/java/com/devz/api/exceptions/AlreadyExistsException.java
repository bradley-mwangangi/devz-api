package com.devz.api.exceptions;

import jakarta.validation.constraints.NotNull;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(@NotNull String message) {
        super(message);
    }
}
