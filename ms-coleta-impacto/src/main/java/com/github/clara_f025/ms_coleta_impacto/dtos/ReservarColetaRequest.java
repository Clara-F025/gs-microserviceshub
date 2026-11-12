package com.github.clara_f025.ms_coleta_impacto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReservarColetaRequest(
        @NotBlank(message = "Campo protocolo é requerido")
        @Size(max = 30, message = "O protocolo deve ter no máximo 30 caracteres")
        String protocolo,

        @NotBlank(message = "Campo idColetor é requerido")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "O idColetor deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos)")
        String idColetor,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao) {
}
