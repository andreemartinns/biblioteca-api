package com.senac.bibliotecaapi.service;

import com.senac.bibliotecaapi.entity.Categoria;
import com.senac.bibliotecaapi.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public Page<Categoria> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Categoria salvar(Categoria categoria) {
        return repository.save(categoria);
    }

    public Optional<Categoria> atualizar(Long id, Categoria dados) {
        return repository.findById(id).map(c -> {
            c.setNome(dados.getNome());
            return repository.save(c);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public Page<Categoria> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }
}