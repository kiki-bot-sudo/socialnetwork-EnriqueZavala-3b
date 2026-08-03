package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.InvalidCredentialsException;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import com.enriquezavala.socialnetwork.security.JwtUtil;
import com.enriquezavala.socialnetwork.web.dto.AuthResponse;
import com.enriquezavala.socialnetwork.web.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil = new JwtUtil(
            "test-secret-key-with-at-least-32-characters", 60_000);
    private final AuthService authService =
            new AuthService(userRepository, passwordEncoder, jwtUtil);

    @Test
    void returnsTokenForValidCredentials() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("1234"));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(new LoginRequest(user.getEmail(), "1234"));

        assertEquals("Bearer", response.type());
        assertEquals(user.getEmail(), jwtUtil.extractEmail(response.token()));
    }

    @Test
    void rejectsInvalidPassword() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("1234"));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest(user.getEmail(), "wrong")));
    }
}
