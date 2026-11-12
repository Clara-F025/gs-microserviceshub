package com.github.clara_f025.ms_coleta_impacto.dtos;

import jakarta.validation.constraints.Size;

public record AtualizarColetaRequest(
        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao) {
}
