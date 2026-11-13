package com.github.clara_f025.ms_gerador_lotes.dtos;

import com.github.clara_f025.ms_gerador_lotes.entities.Lote;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusLote;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoteResponse(
        Long id,
        String protocolo,
        String cnpjGerador,
        String responsavel,
        String telefone,
        String cep,
        String logradouro,
        String bairro,
        String tipoResiduo,
        BigDecimal pesoKg,
        String janelaHorario,
        LocalDateTime dtRegistro,
        StatusLote status,
        String idColetor,
        LocalDateTime dtColeta) {

    public static LoteResponse from(Lote lote) {
        return new LoteResponse(
                lote.getId(),
                lote.getProtocolo(),
                lote.getGerador().getCnpj(),
                lote.getResponsavel(),
                lote.getTelefone(),
                lote.getCep(),
                lote.getLogradouro(),
                lote.getBairro(),
                lote.getTipoResiduo(),
                lote.getPesoKg(),
                lote.getJanelaHorario(),
                lote.getDtRegistro(),
                lote.getStatus(),
                lote.getIdColetor(),
                lote.getDtColeta());
    }
}
