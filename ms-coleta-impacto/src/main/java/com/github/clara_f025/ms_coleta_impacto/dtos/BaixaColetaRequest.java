package com.github.clara_f025.ms_coleta_impacto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** O protocolo do lote é informado na rota: POST /api/v1/coletas/{protocolo}/baixa. */
public record BaixaColetaRequest(
        @NotBlank(message = "Campo idColetor é requerido")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "O idColetor deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos)")
        String idColetor,

        @NotBlank(message = "Campo pinInformado é requerido")
        @Pattern(regexp = "\\d{4}", message = "O PIN deve conter 4 dígitos numéricos")
        String pinInformado) {
}
