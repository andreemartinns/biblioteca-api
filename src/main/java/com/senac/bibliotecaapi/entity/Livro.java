package com.senac.bibliotecaapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

@Schema(description = "Livro pertencente ao acervo da biblioteca, vinculado a uma categoria e um ou mais autores")
@Entity
public class Livro {

    @Schema(description = "Identificador único do livro", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Título do livro", example = "Dom Casmurro", minLength = 1, maxLength = 150, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O título é obrigatório")
    @Size(min = 1, max = 150, message = "O título deve ter entre 1 e 150 caracteres")
    @Column(nullable = false)
    private String titulo;

    @Schema(description = "Código ISBN do livro (apenas números, 10 ou 13 dígitos)", example = "9788535911664", minLength = 10, maxLength = 13, pattern = "^(\\d{10}|\\d{13})$", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O ISBN é obrigatório")
    @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "O ISBN deve conter apenas números (10 ou 13 dígitos)")
    @Column(nullable = false, unique = true)
    private String isbn;

    @Schema(description = "Ano em que o livro foi publicado", example = "1899", minimum = "1000", maximum = "2100")
    @Min(value = 1000, message = "Ano inválido")
    @Max(value = 2100, message = "Ano inválido")
    private Integer anoPublicacao;

    @Schema(description = "Categoria literária do livro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A categoria é obrigatória")
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Schema(description = "Conjunto de autores responsáveis pelo livro (relação muitos-para-muitos)")
    @ManyToMany
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    public Livro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Set<Autor> getAutores() { return autores; }
    public void setAutores(Set<Autor> autores) { this.autores = autores; }
}