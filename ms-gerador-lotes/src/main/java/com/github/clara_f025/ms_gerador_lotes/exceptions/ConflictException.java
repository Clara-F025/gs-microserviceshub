package com.github.clara_f025.ms_gerador_lotes.exceptions;

/** Operação incompatível com o estado atual do recurso (HTTP 409). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
