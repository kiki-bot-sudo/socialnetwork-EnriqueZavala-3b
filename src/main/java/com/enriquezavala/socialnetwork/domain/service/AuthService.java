package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.InvalidCredentialsException;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import com.enriquezavala.socialnetwork.security.JwtUtil;
import com.enriquezavala.socialnetwork.web.dto.AuthResponse;
import com.enriquezavala.socialnetwork.web.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        if (request.email() == null || request.email().isBlank()
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
}
