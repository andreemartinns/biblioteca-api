package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.Usuario;
import com.senac.bibliotecaapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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

@Tag(name = "Usuários", description = "Gerenciamento de usuários da biblioteca")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar usuários", description = "Retorna os usuários de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Usuario>> listar(@ParameterObject Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Buscar usuários por nome", description = "Filtra usuários cujo nome contém o texto informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public PagedModel<EntityModel<Usuario>> buscarPorNome(
            @Parameter(description = "Texto a buscar no nome", example = "Maria") @RequestParam String nome,
            @ParameterObject Pageable pageable) {
        return paraPagedModel(service.buscarPorNome(nome, pageable));
    }

    @Operation(summary = "Buscar usuário por ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(u -> ResponseEntity.ok(comLinks(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar usuário", description = "Cria o usuário e o perfil (1-1) na mesma requisição")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> criar(@Valid @RequestBody Usuario usuario) {
        Usuario salvo = service.salvar(usuario);
        return ResponseEntity.created(URI.create("/usuarios/" + salvo.getId())).body(comLinks(salvo));
    }

    @Operation(summary = "Atualizar usuário")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return service.atualizar(id, usuario)
                .map(u -> ResponseEntity.ok(comLinks(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir usuário")
    @ApiResponse(responseCode = "204", description = "Usuário excluído")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "409", description = "Usuário possui empréstimos")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    private EntityModel<Usuario> comLinks(Usuario u) {
        EntityModel<Usuario> modelo = EntityModel.of(u,
                linkTo(methodOn(UsuarioController.class).buscar(u.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).atualizar(u.getId(), u)).withRel("atualizar"),
                linkTo(methodOn(UsuarioController.class).deletar(u.getId())).withRel("deletar"),
                linkTo(methodOn(UsuarioController.class).listar(Pageable.unpaged())).withRel("usuarios"));

        if (u.getPerfil() != null && u.getPerfil().getId() != null) {
            modelo.add(linkTo(methodOn(PerfilUsuarioController.class).buscar(u.getPerfil().getId()))
                    .withRel("perfil"));
        }
        return modelo;
    }

    private PagedModel<EntityModel<Usuario>> paraPagedModel(Page<Usuario> pagina) {
        List<EntityModel<Usuario>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}