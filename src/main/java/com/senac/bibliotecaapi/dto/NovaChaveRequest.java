package com.senac.bibliotecaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para gerar uma nova chave de API")
public record NovaChaveRequest(

        @Schema(description = "Nome do dono da chave", example = "Biblioteca Municipal",
                minLength = 3, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O dono da chave é obrigatório")
        @Size(min = 3, max = 100, message = "O dono deve ter entre 3 e 100 caracteres")
        String dono
) {
}