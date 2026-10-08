package com.senac.bibliotecaapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Registro de empréstimo de um livro a um usuário, com controle de devolução")
@Entity
public class Emprestimo {

    @Schema(description = "Identificador único do empréstimo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Usuário que realizou o empréstimo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O usuário é obrigatório")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Schema(description = "Livro emprestado", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O livro é obrigatório")
    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Schema(description = "Data em que o empréstimo foi realizado", example = "2026-10-01", format = "date")
    private LocalDate dataEmprestimo = LocalDate.now();

    @Schema(description = "Data prevista para devolução do livro", example = "2026-10-20", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A data prevista de devolução é obrigatória")
    private LocalDate dataPrevista;

    @Schema(description = "Data em que o livro foi efetivamente devolvido (preenchida na devolução)", example = "2026-10-18", format = "date")
    private LocalDate dataDevolucao;

    @Schema(description = "Situação atual do empréstimo", example = "ATIVO")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status = StatusEmprestimo.ATIVO;

    public Emprestimo() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }
}