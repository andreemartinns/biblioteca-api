package com.senac.bibliotecaapi.controller;

import com.senac.bibliotecaapi.entity.Emprestimo;
import com.senac.bibliotecaapi.entity.StatusEmprestimo;
import com.senac.bibliotecaapi.service.EmprestimoService;
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

@Tag(name = "Empréstimos", description = "Controle de empréstimos e devoluções")
@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar empréstimos", description = "Retorna os empréstimos de forma paginada, com links HATEOAS")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public PagedModel<EntityModel<Emprestimo>> listar(@ParameterObject Pageable pageable) {
        return paraPagedModel(service.listar(pageable));
    }

    @Operation(summary = "Filtrar empréstimos por status", description = "Status possíveis: ATIVO, DEVOLVIDO, ATRASADO")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido")
    @GetMapping("/status/{status}")
    public PagedModel<EntityModel<Emprestimo>> buscarPorStatus(
            @Parameter(description = "Status do empréstimo", example = "ATIVO") @PathVariable StatusEmprestimo status,
            @ParameterObject Pageable pageable) {
        return paraPagedModel(service.buscarPorStatus(status, pageable));
    }

    @Operation(summary = "Buscar empréstimo por ID")
    @ApiResponse(responseCode = "200", description = "Empréstimo encontrado")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Emprestimo>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(e -> ResponseEntity.ok(comLinks(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar empréstimo", description = "Cria o empréstimo com status ATIVO")
    @ApiResponse(responseCode = "201", description = "Empréstimo registrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<EntityModel<Emprestimo>> criar(@Valid @RequestBody Emprestimo emprestimo) {
        Emprestimo salvo = service.salvar(emprestimo);
        return ResponseEntity.created(URI.create("/emprestimos/" + salvo.getId())).body(comLinks(salvo));
    }

    @Operation(summary = "Atualizar empréstimo")
    @ApiResponse(responseCode = "200", description = "Empréstimo atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Emprestimo>> atualizar(@PathVariable Long id, @Valid @RequestBody Emprestimo emprestimo) {
        return service.atualizar(id, emprestimo)
                .map(e -> ResponseEntity.ok(comLinks(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar devolução", description = "Preenche a data de devolução e muda o status para DEVOLVIDO")
    @ApiResponse(responseCode = "200", description = "Devolução registrada")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<EntityModel<Emprestimo>> devolver(@PathVariable Long id) {
        return service.devolver(id)
                .map(e -> ResponseEntity.ok(comLinks(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir empréstimo")
    @ApiResponse(responseCode = "204", description = "Empréstimo excluído")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ---------- HATEOAS ----------

    private EntityModel<Emprestimo> comLinks(Emprestimo e) {
        EntityModel<Emprestimo> modelo = EntityModel.of(e,
                linkTo(methodOn(EmprestimoController.class).buscar(e.getId())).withSelfRel(),
                linkTo(methodOn(EmprestimoController.class).atualizar(e.getId(), e)).withRel("atualizar"),
                linkTo(methodOn(EmprestimoController.class).deletar(e.getId())).withRel("deletar"),
                linkTo(methodOn(EmprestimoController.class).listar(Pageable.unpaged())).withRel("emprestimos"));

        // Link condicional: só permite devolver se ainda não foi devolvido
        if (e.getStatus() != StatusEmprestimo.DEVOLVIDO) {
            modelo.add(linkTo(methodOn(EmprestimoController.class).devolver(e.getId())).withRel("devolver"));
        }
        if (e.getUsuario() != null && e.getUsuario().getId() != null) {
            modelo.add(linkTo(methodOn(UsuarioController.class).buscar(e.getUsuario().getId())).withRel("usuario"));
        }
        if (e.getLivro() != null && e.getLivro().getId() != null) {
            modelo.add(linkTo(methodOn(LivroController.class).buscar(e.getLivro().getId())).withRel("livro"));
        }
        return modelo;
    }

    private PagedModel<EntityModel<Emprestimo>> paraPagedModel(Page<Emprestimo> pagina) {
        List<EntityModel<Emprestimo>> itens = pagina.getContent().stream()
                .map(this::comLinks)
                .toList();
        PagedModel.PageMetadata meta = new PagedModel.PageMetadata(
                pagina.getSize(), pagina.getNumber(), pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}