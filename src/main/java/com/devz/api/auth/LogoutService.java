package com.devz.api.auth;

import com.devz.api.constants.Constants;
import com.devz.api.jwtToken.JwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(Constants.AUTHORIZATION);
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        var tokenInStorage = jwtTokenRepository.findByToken(jwt)
                .orElse(null);

        if (tokenInStorage != null) {
            tokenInStorage.setExpired(true);
            tokenInStorage.setRevoked(true);

            jwtTokenRepository.save(tokenInStorage);
            SecurityContextHolder.clearContext();

            response.setHeader("Access-Control-Allow-Methods", "POST");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }
    }
}
