package com.senac.bibliotecaapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Autor responsável pela escrita de um ou mais livros do acervo")
@Entity
public class Autor {

    @Schema(description = "Identificador único do autor", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome completo do autor", example = "Machado de Assis", minLength = 3, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O nome do autor é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false)
    private String nome;

    @Schema(description = "Nacionalidade do autor", example = "Brasileira", maxLength = 50, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "A nacionalidade deve ter no máximo 50 caracteres")
    private String nacionalidade;

    public Autor() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
}