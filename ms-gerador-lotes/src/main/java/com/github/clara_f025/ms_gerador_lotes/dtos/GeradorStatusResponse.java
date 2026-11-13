package com.github.clara_f025.ms_gerador_lotes.dtos;

import com.github.clara_f025.ms_gerador_lotes.entities.StatusGerador;

public record GeradorStatusResponse(
        Long id,
        String cnpj,
        StatusGerador status,
        boolean agendamentoLiberado) {
}
