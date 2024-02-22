package com.devz.api.actors.appUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUser> getUserById(@PathVariable("userId") UUID userId) {
        AppUser foundUser = userService.getUserById(userId);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public UserDetails getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }
}
