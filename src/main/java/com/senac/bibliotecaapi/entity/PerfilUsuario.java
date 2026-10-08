package com.senac.bibliotecaapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Dados complementares de contato e nascimento de um usuário")
@Entity
public class PerfilUsuario {

    @Schema(description = "Identificador único do perfil", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Telefone de contato do usuário (apenas números, com DDD)", example = "11999999999", minLength = 10, maxLength = 11, pattern = "^\\d{10,11}$")
    @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter apenas números (10 ou 11 dígitos, com DDD)")
    private String telefone;

    @Schema(description = "Endereço residencial do usuário", example = "Rua A, 100", maxLength = 200)
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
    private String endereco;

    @Schema(description = "Data de nascimento do usuário, deve estar no passado", example = "2000-05-10", format = "date")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    public PerfilUsuario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}