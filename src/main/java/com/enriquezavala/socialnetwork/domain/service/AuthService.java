package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.InvalidCredentialsException;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import com.enriquezavala.socialnetwork.security.FirebaseTokenVerifier;
import com.enriquezavala.socialnetwork.security.FirebaseTokenVerifier.FirebaseUser;
import com.enriquezavala.socialnetwork.security.JwtUtil;
import com.enriquezavala.socialnetwork.web.dto.AuthResponse;
import com.enriquezavala.socialnetwork.web.dto.FirebaseLoginRequest;
import com.enriquezavala.socialnetwork.web.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthService {

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_.]{3,50}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, FirebaseTokenVerifier firebaseTokenVerifier) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.firebaseTokenVerifier = firebaseTokenVerifier;
    }

    /** Login clásico con correo y contraseña guardados en el backend (Swagger, scripts). */
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        }

        return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
    }

    /**
     * Login con Firebase: la app envía el ID token de Firebase; si es válido se
     * entrega un JWT del backend. Si el correo aún no tiene cuenta aquí, se crea
     * (con el username pedido o uno derivado del correo). La contraseña la
     * administra Firebase, así que la recuperación de contraseña no desincroniza nada.
     */
    public AuthResponse loginWithFirebase(FirebaseLoginRequest request) {
        if (request == null) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos");
        }
        FirebaseUser firebaseUser = firebaseTokenVerifier.verify(request.idToken());

        Optional<User> existing = userRepository.findByEmail(firebaseUser.email());
        if (existing.isEmpty()) {
            createUserFor(firebaseUser.email(), request.username(), request.name());
        }
        return new AuthResponse(jwtUtil.generateToken(firebaseUser.email()));
    }

    private void createUserFor(String email, String requestedUsername, String name) {
        String username;
        if (requestedUsername != null && !requestedUsername.isBlank()) {
            username = requestedUsername.trim();
            if (!username.matches(USERNAME_PATTERN)) {
                throw new IllegalArgumentException(
                        "El username debe tener de 3 a 50 letras, números, punto o guion bajo");
            }
            if (userRepository.existsByUsername(username)) {
                throw new IllegalStateException("El username ya está registrado");
            }
        } else {
            username = availableUsernameFrom(email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name == null || name.isBlank() ? null : name.trim());
        // La contraseña vive en Firebase; aquí se guarda una aleatoria que nadie conoce.
        byte[] secret = new byte[32];
        random.nextBytes(secret);
        user.setPassword(passwordEncoder.encode(Base64.getEncoder().encodeToString(secret)));
        userRepository.save(user);
    }

    private String availableUsernameFrom(String email) {
        String base = email.split("@")[0].replaceAll("[^A-Za-z0-9_.]", "_");
        if (base.length() < 3) base = base + "_user";
        if (base.length() > 40) base = base.substring(0, 40);
        String candidate = base;
        for (int i = 2; userRepository.existsByUsername(candidate); i++) {
            candidate = base + "_" + i;
        }
        return candidate;
    }
}
