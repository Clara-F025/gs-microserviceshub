package com.github.clara_f025.ms_gerador_lotes.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarLoteRequest(
        @NotBlank(message = "Campo cnpj é requerido")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos")
        String cnpj,

        @NotBlank(message = "Campo responsavel é requerido")
        @Size(min = 3, max = 100, message = "O responsável deve ter entre 3 e 100 caracteres")
        String responsavel,

        @NotBlank(message = "Campo telefone é requerido")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
        String telefone,

        @NotBlank(message = "Campo cep é requerido")
        @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos")
        String cep,

        @NotBlank(message = "Campo tipoResiduo é requerido")
        @Size(max = 30, message = "O tipo de resíduo deve ter no máximo 30 caracteres")
        String tipoResiduo,

        @NotNull(message = "O campo pesoKg é requerido")
        @DecimalMin(value = "10.0", message = "Peso mínimo de 10kg")
        BigDecimal pesoKg,

        @NotBlank(message = "Campo janelaHorario é requerido")
        @Size(max = 30, message = "A janela de horário deve ter no máximo 30 caracteres")
        String janelaHorario) {
}
