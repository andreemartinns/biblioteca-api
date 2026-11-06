package com.senac.bibliotecaapi.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Schema(description = "Chave de API usada para autenticar requisições de escrita")
@Entity
public class ChaveApi {

    @Schema(description = "Identificador único da chave", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Valor da chave, enviado no cabeçalho X-API-Key", example = "a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false, unique = true)
    private String chave;

    @Schema(description = "Nome do dono da chave", example = "Biblioteca Municipal", minLength = 3, maxLength = 100)
    @Column(nullable = false)
    private String dono;

    @Schema(description = "Nível de acesso: USUARIO (cria e altera) ou ADMIN (também exclui e gerencia chaves)", example = "USUARIO")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAcesso nivel = NivelAcesso.USUARIO;

    @Schema(description = "Indica se a chave está ativa (false = revogada)", example = "true")
    private boolean ativa = true;

    @Schema(description = "Data e hora de criação da chave", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime criadaEm = LocalDateTime.now();

    public ChaveApi() {
    }

    public ChaveApi(String chave, String dono, NivelAcesso nivel) {
        this.chave = chave;
        this.dono = dono;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }

    public String getDono() { return dono; }
    public void setDono(String dono) { this.dono = dono; }

    public NivelAcesso getNivel() { return nivel; }
    public void setNivel(NivelAcesso nivel) { this.nivel = nivel; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDateTime getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDateTime criadaEm) { this.criadaEm = criadaEm; }
}