package com.devz.api.auth;

import lombok.Builder;

@Builder
public record AuthTokenPairResponse(String accessToken, String refreshToken) {
}
