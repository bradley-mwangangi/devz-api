package com.devz.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthenticationRequest {

    @Email(message = "invalid email")
    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email must not be empty")
    private String email;

    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password must not be empty")
    private String password;
}
