package com.github.clara_f025.ms_gerador_lotes.dtos;

import com.github.clara_f025.ms_gerador_lotes.entities.Gerador;
import com.github.clara_f025.ms_gerador_lotes.entities.StatusGerador;

import java.time.LocalDateTime;

public record GeradorResponse(
        String cnpj,
        String razaoSocial,
        String cep,
        String telefone,
        StatusGerador status,
        LocalDateTime dtCadastro) {

    public static GeradorResponse from(Gerador gerador) {
        return new GeradorResponse(
                gerador.getCnpj(),
                gerador.getRazaoSocial(),
                gerador.getCep(),
                gerador.getTelefone(),
                gerador.getStatus(),
                gerador.getDtCadastro());
    }
}
