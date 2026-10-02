package com.senac.bibliotecaapi.service;

import com.senac.bibliotecaapi.entity.Livro;
import com.senac.bibliotecaapi.repository.LivroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Page<Livro> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Livro> buscarPorTitulo(String titulo, Pageable pageable) {
        return repository.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    public Optional<Livro> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Livro salvar(Livro livro) {
        return repository.save(livro);
    }

    public Optional<Livro> atualizar(Long id, Livro dados) {
        return repository.findById(id).map(l -> {
            l.setTitulo(dados.getTitulo());
            l.setIsbn(dados.getIsbn());
            l.setAnoPublicacao(dados.getAnoPublicacao());
            l.setCategoria(dados.getCategoria());
            l.setAutores(dados.getAutores());
            return repository.save(l);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}