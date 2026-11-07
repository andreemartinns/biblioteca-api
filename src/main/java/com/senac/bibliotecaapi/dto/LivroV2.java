package com.senac.bibliotecaapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representação resumida de um livro (API versão 2)")
public record LivroV2(
        @Schema(description = "Identificador do livro", example = "1") Long id,
        @Schema(description = "Título do livro", example = "Dom Casmurro") String titulo,
        @Schema(description = "Código ISBN", example = "9788535911664") String isbn,
        @Schema(description = "Ano de publicação", example = "1899") Integer anoPublicacao,
        @Schema(description = "Nome da categoria", example = "Romance") String categoria,
        @Schema(description = "Nomes dos autores", example = "[\"Machado de Assis\"]") List<String> autores
) {
}