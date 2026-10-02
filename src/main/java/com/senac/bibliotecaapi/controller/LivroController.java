package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.Livro;
import com.senac.bibliotecaapi.service.LivroService;
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

@Tag(name = "Livros", description = "Gerenciamento do acervo de livros")
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @Operation(summary = "Listar livros", description = "Retorna os livros de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Livro>> listar(Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Buscar livros por título", description = "Filtra livros cujo título contém o texto informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public PagedModel<EntityModel<Livro>> buscarPorTitulo(@RequestParam String titulo, Pageable pageable) {
        return paraPagedModel(service.buscarPorTitulo(titulo, pageable));
    }

    @Operation(summary = "Buscar livro por ID")
    @ApiResponse(responseCode = "200", description = "Livro encontrado")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Livro>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(l -> ResponseEntity.ok(comLinks(l)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar livro", description = "Exige categoria existente e aceita vários autores")
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "ISBN já cadastrado")
    @PostMapping
    public ResponseEntity<EntityModel<Livro>> criar(@Valid @RequestBody Livro livro) {
        Livro salvo = service.salvar(livro);
        return ResponseEntity.created(URI.create("/livros/" + salvo.getId())).body(comLinks(salvo));
    }

    @Operation(summary = "Atualizar livro")
    @ApiResponse(responseCode = "200", description = "Livro atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Livro>> atualizar(@PathVariable Long id, @Valid @RequestBody Livro livro) {
        return service.atualizar(id, livro)
                .map(l -> ResponseEntity.ok(comLinks(l)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir livro")
    @ApiResponse(responseCode = "204", description = "Livro excluído")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @ApiResponse(responseCode = "409", description = "Livro possui empréstimos")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    private EntityModel<Livro> comLinks(Livro l) {
        EntityModel<Livro> modelo = EntityModel.of(l,
                linkTo(methodOn(LivroController.class).buscar(l.getId())).withSelfRel(),
                linkTo(methodOn(LivroController.class).atualizar(l.getId(), l)).withRel("atualizar"),
                linkTo(methodOn(LivroController.class).deletar(l.getId())).withRel("deletar"),
                linkTo(methodOn(LivroController.class).listar(Pageable.unpaged())).withRel("livros"));

        // Navegação para a categoria do livro
        if (l.getCategoria() != null && l.getCategoria().getId() != null) {
            modelo.add(linkTo(methodOn(CategoriaController.class).buscar(l.getCategoria().getId()))
                    .withRel("categoria"));
        }
        return modelo;
    }

    private PagedModel<EntityModel<Livro>> paraPagedModel(Page<Livro> pagina) {
        List<EntityModel<Livro>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}