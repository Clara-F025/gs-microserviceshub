package com.github.clara_f025.ms_coleta_impacto.exceptions;

/** Falha de comunicação com outro microsserviço ou circuito aberto (HTTP 503). */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
