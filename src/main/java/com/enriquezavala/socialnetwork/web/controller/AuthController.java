package com.enriquezavala.socialnetwork.web.controller;

import com.enriquezavala.socialnetwork.domain.service.AuthService;
import com.enriquezavala.socialnetwork.web.dto.AuthResponse;
import com.enriquezavala.socialnetwork.web.dto.FirebaseLoginRequest;
import com.enriquezavala.socialnetwork.web.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Log in and obtain a JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Log in with a Firebase ID token and obtain a JWT",
            description = "Verifies the Firebase ID token. If the e-mail has no account yet, "
                    + "one is created (username and name are only used in that case)."
    )
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token")
    @ApiResponse(responseCode = "409", description = "Requested username already taken")
    @PostMapping("/firebase")
    public ResponseEntity<AuthResponse> loginWithFirebase(@RequestBody FirebaseLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithFirebase(request));
    }
}
