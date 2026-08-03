package com.enriquezavala.socialnetwork.web.dto;

public record AuthResponse(String token, String type) {

    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}
