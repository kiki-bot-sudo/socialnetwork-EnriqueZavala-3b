package com.enriquezavala.socialnetwork.domain.exception;

/**
 * Se lanza cuando el usuario autenticado intenta modificar un recurso que no le pertenece.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
