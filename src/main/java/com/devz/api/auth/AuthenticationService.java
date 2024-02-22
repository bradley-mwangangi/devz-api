package com.devz.api.auth;

import com.devz.api.actors.appUser.AppUser;
import com.devz.api.actors.appUser.UserRepository;
import com.devz.api.actors.appUser.UserService;
import com.devz.api.jwtToken.JwtToken;
import com.devz.api.jwtToken.JwtTokenRepository;
import com.devz.api.jwtToken.JwtTokenService;
import com.devz.api.jwtToken.JwtTokenType;
import com.devz.api.role.Role;
import com.devz.api.role.RoleEnum;
import com.devz.api.role.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final JwtTokenRepository tokenRepository;
    private final JwtTokenService jwtService;

    public AppUser signup(SignupRequest signupRequest) {

        userService.assertUserIsNotRegistered(signupRequest.getEmail());

        Role defaultRole = roleRepository.findByRoleName(RoleEnum.USER.name());

        AppUser newUser = AppUser.builder()
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .roles(new HashSet<>(Collections.singleton(defaultRole)))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(newUser);
    }

    // takes in a user object and returns a map of roles: userRoles
    private Map<String, Object> mapRolesAsExtraClaims(AppUser user) {
        Map<String, Object> rolesAsExtraClaims = new HashMap<>();
        rolesAsExtraClaims.put("roles", user.getRoles());

        return rolesAsExtraClaims;
    }

    AuthTokenPairResponse authenticate(
            AuthenticationRequest authRequest
    ) {

        UserDetails userInDB = userService.loadUserByUsername(authRequest.getEmail());

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        var accessToken = jwtService.generateToken(
                mapRolesAsExtraClaims((AppUser) userInDB),
                userInDB
        );
        var refreshToken = jwtService.generateRefreshToken(userInDB);

        revokeAllUserTokens((AppUser) userInDB);
        saveUserToken((AppUser) userInDB, accessToken);

        return AuthTokenPairResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    void saveUserToken(AppUser user, String jwtToken) {
        var token = JwtToken.builder()
                .token(jwtToken)
                .jwtTokenType(JwtTokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(token);
    }

    void revokeAllUserTokens(AppUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(jwtToken -> {
            jwtToken.setExpired(true);
            jwtToken.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    AuthTokenPairResponse refreshToken(
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            UserDetails userInDB = userService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(refreshToken, userInDB)) {
                var accessToken = jwtService.generateToken(
                        mapRolesAsExtraClaims((AppUser) userInDB),
                        userInDB
                );

                revokeAllUserTokens((AppUser) userInDB);
                saveUserToken((AppUser) userInDB, accessToken);

                return AuthTokenPairResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        return null;
    }
}
