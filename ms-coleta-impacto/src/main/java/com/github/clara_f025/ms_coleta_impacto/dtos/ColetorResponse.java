package com.github.clara_f025.ms_coleta_impacto.dtos;

import com.github.clara_f025.ms_coleta_impacto.entities.Coletor;

import java.math.BigDecimal;

public record ColetorResponse(
        Long id,
        String cpfCnpj,
        String nome,
        String telefone,
        String cepAtuacao,
        BigDecimal saldoKg,
        BigDecimal co2EvitadoKg,
        Long pontosAcumulados) {

    public static ColetorResponse from(Coletor coletor) {
        return new ColetorResponse(
                coletor.getId(),
                coletor.getCpfCnpj(),
                coletor.getNome(),
                coletor.getTelefone(),
                coletor.getCepAtuacao(),
                coletor.getSaldoKg(),
                coletor.getCo2EvitadoKg(),
                coletor.getPontosAcumulados());
    }
}
