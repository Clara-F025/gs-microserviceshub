package com.github.clara_f025.ms_coleta_impacto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ColetorRequest(
        @NotBlank(message = "Campo cpfCnpj é requerido")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "O cpfCnpj deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos)")
        String cpfCnpj,

        @NotBlank(message = "Campo nome é requerido")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "Campo telefone é requerido")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
        String telefone,

        @NotBlank(message = "Campo cepAtuacao é requerido")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos")
        String cepAtuacao) {
}
