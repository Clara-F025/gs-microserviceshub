package com.github.clara_f025.ms_coleta_impacto.dtos;

import java.math.BigDecimal;

public record ReciboImpactoResponse(
        boolean sucesso,
        String protocolo,
        BigDecimal pesoKg,
        BigDecimal co2EvitadoKg,
        Long pontosGanhos,
        String mensagem) {
}
