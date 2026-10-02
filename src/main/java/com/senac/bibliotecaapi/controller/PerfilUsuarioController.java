package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.PerfilUsuario;
import com.senac.bibliotecaapi.service.PerfilUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Perfis", description = "Dados complementares dos usuários")
@RestController
@RequestMapping("/perfis")
public class PerfilUsuarioController {

    private final PerfilUsuarioService service;

    public PerfilUsuarioController(PerfilUsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar perfis", description = "Retorna os perfis de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<PerfilUsuario>> listar(Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Buscar perfis por telefone", description = "Filtra perfis cujo telefone contém o texto informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public PagedModel<EntityModel<PerfilUsuario>> buscarPorTelefone(@RequestParam String telefone, Pageable pageable) {
        return paraPagedModel(service.buscarPorTelefone(telefone, pageable));
    }

    @Operation(summary = "Buscar perfil por ID")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PerfilUsuario>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(p -> ResponseEntity.ok(comLinks(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar perfil")
    @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<EntityModel<PerfilUsuario>> criar(@Valid @RequestBody PerfilUsuario perfil) {
        PerfilUsuario salvo = service.salvar(perfil);
        return ResponseEntity.created(URI.create("/perfis/" + salvo.getId())).body(comLinks(salvo));
    }

    @Operation(summary = "Atualizar perfil")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PerfilUsuario>> atualizar(@PathVariable Long id, @Valid @RequestBody PerfilUsuario perfil) {
        return service.atualizar(id, perfil)
                .map(p -> ResponseEntity.ok(comLinks(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir perfil")
    @ApiResponse(responseCode = "204", description = "Perfil excluído")
    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    private EntityModel<PerfilUsuario> comLinks(PerfilUsuario p) {
        return EntityModel.of(p,
                linkTo(methodOn(PerfilUsuarioController.class).buscar(p.getId())).withSelfRel(),
                linkTo(methodOn(PerfilUsuarioController.class).atualizar(p.getId(), p)).withRel("atualizar"),
                linkTo(methodOn(PerfilUsuarioController.class).deletar(p.getId())).withRel("deletar"),
                linkTo(methodOn(PerfilUsuarioController.class).listar(Pageable.unpaged())).withRel("perfis"));
    }

    private PagedModel<EntityModel<PerfilUsuario>> paraPagedModel(Page<PerfilUsuario> pagina) {
        List<EntityModel<PerfilUsuario>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}