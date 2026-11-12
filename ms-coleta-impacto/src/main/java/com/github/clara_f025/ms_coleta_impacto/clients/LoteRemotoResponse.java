package com.github.clara_f025.ms_coleta_impacto.clients;

import java.math.BigDecimal;

/** Visão do ms-coleta-impacto sobre o recurso Lote exposto pelo ms-gerador-lotes. */
public record LoteRemotoResponse(
        String protocolo,
        String cep,
        String bairro,
        String tipoResiduo,
        BigDecimal pesoKg,
        String janelaHorario,
        String status,
        String idColetor) {
}
