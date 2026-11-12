package com.github.clara_f025.ms_gerador_lotes.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GeradorRequest(
        @NotBlank(message = "Campo cnpj é requerido")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos")
        String cnpj,

        @NotBlank(message = "Campo razaoSocial é requerido")
        @Size(min = 3, max = 150, message = "A razão social deve ter entre 3 e 150 caracteres")
        String razaoSocial,

        @NotBlank(message = "Campo cep é requerido")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos")
        String cep,

        @NotBlank(message = "Campo telefone é requerido")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
        String telefone) {
}
