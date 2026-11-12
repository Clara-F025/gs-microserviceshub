package com.github.clara_f025.ms_coleta_impacto.dtos;

import java.math.BigDecimal;

public record LoteDisponivelDTO(
        String protocolo,
        String cep,
        String bairro,
        String tipoResiduo,
        BigDecimal pesoKg,
        String janelaHorario) {
}
