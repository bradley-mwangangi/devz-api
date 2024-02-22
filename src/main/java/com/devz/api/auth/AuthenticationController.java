package com.devz.api.auth;

import com.devz.api.actors.appUser.AppUser;
import com.devz.api.constants.Constants;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AppUser newUser = authService.signup(signupRequest);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(
            @RequestBody @Valid AuthenticationRequest authRequest,
            HttpServletResponse response
    ) {
        AuthTokenPairResponse authTokenResponse = authService.authenticate(authRequest);
        response.setHeader(Constants.AUTHORIZATION,
                AUTHORIZATION_HEADER_PREFIX + authTokenResponse.accessToken());
        response.setHeader(Constants.REFRESH_TOKEN,
                AUTHORIZATION_HEADER_PREFIX + authTokenResponse.refreshToken());

        return ResponseEntity.ok().build();
    }
}
