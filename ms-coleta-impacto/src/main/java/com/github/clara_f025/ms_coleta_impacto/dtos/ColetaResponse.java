package com.github.clara_f025.ms_coleta_impacto.dtos;

import com.github.clara_f025.ms_coleta_impacto.entities.Coleta;
import com.github.clara_f025.ms_coleta_impacto.entities.StatusColeta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ColetaResponse(
        Long id,
        String protocoloLote,
        String idColetor,
        BigDecimal pesoKg,
        BigDecimal co2EvitadoKg,
        Long pontosGanhos,
        StatusColeta status,
        LocalDateTime dtReserva,
        LocalDateTime dtConclusao,
        String observacao) {

    public static ColetaResponse from(Coleta coleta) {
        return new ColetaResponse(
                coleta.getId(),
                coleta.getProtocoloLote(),
                coleta.getColetor().getCpfCnpj(),
                coleta.getPesoKg(),
                coleta.getCo2EvitadoKg(),
                coleta.getPontosGanhos(),
                coleta.getStatus(),
                coleta.getDtReserva(),
                coleta.getDtConclusao(),
                coleta.getObservacao());
    }
}
