package com.enriquezavala.socialnetwork.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "test-secret-key-with-at-least-32-characters", 60_000);

    @Test
    void generatesAndValidatesToken() {
        String email = "user@example.com";

        String token = jwtUtil.generateToken(email);

        assertEquals(email, jwtUtil.extractEmail(token));
        assertTrue(jwtUtil.isTokenValid(token, email));
    }
}
