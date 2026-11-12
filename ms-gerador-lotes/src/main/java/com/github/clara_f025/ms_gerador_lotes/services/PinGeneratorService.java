package com.github.clara_f025.ms_gerador_lotes.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PinGeneratorService {

    private final SecureRandom random = new SecureRandom();

    /** Gera um PIN numérico aleatório de 4 dígitos. */
    public String gerarPin() {
        return String.format("%04d", random.nextInt(10000));
    }
}
