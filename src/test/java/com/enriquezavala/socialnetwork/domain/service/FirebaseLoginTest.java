package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.InvalidCredentialsException;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import com.enriquezavala.socialnetwork.security.FirebaseTokenVerifier;
import com.enriquezavala.socialnetwork.security.FirebaseTokenVerifier.FirebaseUser;
import com.enriquezavala.socialnetwork.security.JwtUtil;
import com.enriquezavala.socialnetwork.web.dto.AuthResponse;
import com.enriquezavala.socialnetwork.web.dto.FirebaseLoginRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseLoginTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final FirebaseTokenVerifier verifier = mock(FirebaseTokenVerifier.class);
    private final JwtUtil jwtUtil = new JwtUtil("test-secret-key-with-at-least-32-characters", 60_000);
    private final AuthService authService =
            new AuthService(userRepository, new BCryptPasswordEncoder(), jwtUtil, verifier);

    @Test
    void existingUserGetsTokenWithoutCreatingAccount() {
        when(verifier.verify("good")).thenReturn(new FirebaseUser("uid1", "ana@example.com"));
        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(new User()));

        AuthResponse response = authService.loginWithFirebase(
                new FirebaseLoginRequest("good", null, null));

        assertEquals("ana@example.com", jwtUtil.extractEmail(response.token()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void newUserIsCreatedWithRequestedUsername() {
        when(verifier.verify("good")).thenReturn(new FirebaseUser("uid2", "nuevo@example.com"));
        when(userRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.empty());

        authService.loginWithFirebase(new FirebaseLoginRequest("good", "nuevo_user", "Nuevo"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("nuevo_user", saved.getValue().getUsername());
        assertEquals("nuevo@example.com", saved.getValue().getEmail());
        assertEquals("Nuevo", saved.getValue().getName());
        assertNotNull(saved.getValue().getPassword());
    }

    @Test
    void usernameIsDerivedFromEmailWhenMissing() {
        when(verifier.verify("good")).thenReturn(new FirebaseUser("uid3", "maria.p@example.com"));
        when(userRepository.findByEmail("maria.p@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("maria.p")).thenReturn(true);

        authService.loginWithFirebase(new FirebaseLoginRequest("good", null, null));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("maria.p_2", saved.getValue().getUsername());
    }

    @Test
    void takenUsernameIsRejected() {
        when(verifier.verify("good")).thenReturn(new FirebaseUser("uid4", "otro@example.com"));
        when(userRepository.findByEmail("otro@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("ana_foto")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> authService.loginWithFirebase(
                new FirebaseLoginRequest("good", "ana_foto", null)));
        verify(userRepository, never()).save(any());
    }

    @Test
    void invalidFirebaseTokenIsRejected() {
        when(verifier.verify("bad")).thenThrow(new InvalidCredentialsException("Correo o contraseña incorrectos"));

        assertThrows(InvalidCredentialsException.class, () -> authService.loginWithFirebase(
                new FirebaseLoginRequest("bad", null, null)));
    }
}
