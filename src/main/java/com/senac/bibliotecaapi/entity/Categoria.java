package com.senac.bibliotecaapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Categoria literária usada para classificar os livros do acervo")
@Entity
public class Categoria {

    @Schema(description = "Identificador único da categoria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nome da categoria literária (apenas letras, espaços, ponto, apóstrofo e hífen)", example = "Romance", minLength = 3, maxLength = 50, pattern = "^[\\p{L}][\\p{L} .'-]*$", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[\\p{L}][\\p{L} .'-]*$", message = "O nome da categoria deve conter apenas letras")
    @Column(nullable = false, unique = true)
    private String nome;

    public Categoria() {
    }

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}