package com.github.clara_f025.ms_gerador_lotes.clients;

public record ViaCepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro) {
}
