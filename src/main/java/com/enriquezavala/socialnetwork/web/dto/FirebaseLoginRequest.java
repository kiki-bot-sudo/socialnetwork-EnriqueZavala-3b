package com.enriquezavala.socialnetwork.web.dto;

/**
 * Login con Firebase.
 *
 * @param idToken  ID token que entrega Firebase Authentication al iniciar sesión
 * @param username opcional; solo se usa si hay que crear la cuenta en el backend
 * @param name     opcional; solo se usa si hay que crear la cuenta en el backend
 */
public record FirebaseLoginRequest(String idToken, String username, String name) {
}
