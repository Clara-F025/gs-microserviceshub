package com.github.clara_f025.ms_gerador_lotes.dtos;

import com.github.clara_f025.ms_gerador_lotes.entities.StatusLote;

import java.time.LocalDateTime;

public record LoteCriadoResponse(
        Long id,
        String protocolo,
        String pin,
        LocalDateTime dtRegistro,
        StatusLote status) {
}
