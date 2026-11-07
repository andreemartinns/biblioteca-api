package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.dto.NovaChaveRequest;
import com.senac.bibliotecaapi.security.ChaveApi;
import com.senac.bibliotecaapi.security.ChaveApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Chaves de API", description = "Geração e gerenciamento das chaves usadas no cabeçalho X-API-Key")
@RestController
@RequestMapping("/api-keys")
public class ChaveApiController {

    private final ChaveApiService service;

    public ChaveApiController(ChaveApiService service) {
        this.service = service;
    }

    @Operation(summary = "Gerar chave de API",
            description = "Rota pública. Gera uma chave de nível USUARIO, que permite criar e alterar registros.")
    @ApiResponse(responseCode = "201", description = "Created: chave gerada com sucesso")
    @ApiResponse(responseCode = "400", description = "Bad Request: dados inválidos")
    @PostMapping
    public ResponseEntity<ChaveApi> criar(@Valid @RequestBody NovaChaveRequest request) {
        ChaveApi chave = service.criar(request.dono());
        return ResponseEntity.created(URI.create("/api-keys/" + chave.getId())).body(chave);
    }

    @Operation(summary = "Listar chaves", description = "Exige chave ADMIN. Lista as chaves de forma paginada.")
    @ApiResponse(responseCode = "200", description = "OK: lista retornada")
    @ApiResponse(responseCode = "401", description = "Unauthorized: chave ausente ou inválida")
    @ApiResponse(responseCode = "403", description = "Forbidden: chave sem nível ADMIN")
    @GetMapping
    public Page<ChaveApi> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @Operation(summary = "Revogar chave", description = "Exige chave ADMIN. Desativa a chave, que deixa de funcionar.")
    @ApiResponse(responseCode = "200", description = "OK: chave revogada")
    @ApiResponse(responseCode = "401", description = "Unauthorized: chave ausente ou inválida")
    @ApiResponse(responseCode = "403", description = "Forbidden: chave sem nível ADMIN")
    @ApiResponse(responseCode = "404", description = "Not Found: chave não encontrada")
    @PatchMapping("/{id}/revogar")
    public ResponseEntity<ChaveApi> revogar(@PathVariable Long id) {
        return service.revogar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}