package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.Categoria;
import com.senac.bibliotecaapi.service.CategoriaService;
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

@Tag(name = "Categorias", description = "Gerenciamento de categorias de livros")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar categorias", description = "Retorna as categorias de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Categoria>> listar(Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Buscar categorias por nome", description = "Filtra categorias cujo nome contém o texto informado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/busca")
    public PagedModel<EntityModel<Categoria>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return paraPagedModel(service.buscarPorNome(nome, pageable));
    }

    @Operation(summary = "Buscar categoria por ID")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(c -> ResponseEntity.ok(comLinks(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar categoria")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Nome de categoria já existe")
    @PostMapping
    public ResponseEntity<EntityModel<Categoria>> criar(@Valid @RequestBody Categoria categoria) {
        Categoria salva = service.salvar(categoria);
        return ResponseEntity.created(URI.create("/categorias/" + salva.getId())).body(comLinks(salva));
    }

    @Operation(summary = "Atualizar categoria")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> atualizar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        return service.atualizar(id, categoria)
                .map(c -> ResponseEntity.ok(comLinks(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir categoria")
    @ApiResponse(responseCode = "204", description = "Categoria excluída")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Categoria em uso por algum livro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    // Adiciona os links a uma categoria
    private EntityModel<Categoria> comLinks(Categoria c) {
        return EntityModel.of(c,
                linkTo(methodOn(CategoriaController.class).buscar(c.getId())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).atualizar(c.getId(), c)).withRel("atualizar"),
                linkTo(methodOn(CategoriaController.class).deletar(c.getId())).withRel("deletar"),
                linkTo(methodOn(CategoriaController.class).listar(Pageable.unpaged())).withRel("categorias"));
    }

    // Converte uma página em PagedModel com links
    private PagedModel<EntityModel<Categoria>> paraPagedModel(Page<Categoria> pagina) {
        List<EntityModel<Categoria>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}