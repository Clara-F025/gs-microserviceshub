package com.github.clara_f025.ms_gerador_lotes.exceptions;

/** Violação de regra de negócio ou dado inválido (HTTP 400). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
