package com.github.clara_f025.ms_gerador_lotes.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ReservarLoteRequest(
        @NotBlank(message = "Campo idColetor é requerido")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "O idColetor deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos)")
        String idColetor) {
}
