package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.Autor;
import com.senac.bibliotecaapi.service.AutorService;
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

@Tag(name = "Autores", description = "Gerenciamento de autores de livros")
@RestController
@RequestMapping("/autores")
public class AutorController {

    private final AutorService service;

    public AutorController(AutorService service) {
        this.service = service;
    }

    @Operation(summary = "Listar autores", description = "Retorna os autores de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Autor>> listar(Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Buscar autores por nome", description = "Filtra autores cujo nome contém o texto informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public PagedModel<EntityModel<Autor>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return paraPagedModel(service.buscarPorNome(nome, pageable));
    }

    @Operation(summary = "Buscar autor por ID")
    @ApiResponse(responseCode = "200", description = "Autor encontrado")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Autor>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(a -> ResponseEntity.ok(comLinks(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar autor")
    @ApiResponse(responseCode = "201", description = "Autor criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<EntityModel<Autor>> criar(@Valid @RequestBody Autor autor) {
        Autor salvo = service.salvar(autor);
        return ResponseEntity.created(URI.create("/autores/" + salvo.getId())).body(comLinks(salvo));
    }

    @Operation(summary = "Atualizar autor")
    @ApiResponse(responseCode = "200", description = "Autor atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Autor>> atualizar(@PathVariable Long id, @Valid @RequestBody Autor autor) {
        return service.atualizar(id, autor)
                .map(a -> ResponseEntity.ok(comLinks(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir autor")
    @ApiResponse(responseCode = "204", description = "Autor excluído")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    private EntityModel<Autor> comLinks(Autor a) {
        return EntityModel.of(a,
                linkTo(methodOn(AutorController.class).buscar(a.getId())).withSelfRel(),
                linkTo(methodOn(AutorController.class).atualizar(a.getId(), a)).withRel("atualizar"),
                linkTo(methodOn(AutorController.class).deletar(a.getId())).withRel("deletar"),
                linkTo(methodOn(AutorController.class).listar(Pageable.unpaged())).withRel("autores"));
    }

    private PagedModel<EntityModel<Autor>> paraPagedModel(Page<Autor> pagina) {
        List<EntityModel<Autor>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}