package com.senac.bibliotecaapi.service;

import com.senac.bibliotecaapi.entity.Autor;
import com.senac.bibliotecaapi.repository.AutorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutorService {

    private final AutorRepository repository;

    public AutorService(AutorRepository repository) {
        this.repository = repository;
    }

    public Page<Autor> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Autor> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Autor salvar(Autor autor) {
        return repository.save(autor);
    }

    public Optional<Autor> atualizar(Long id, Autor dados) {
        return repository.findById(id).map(a -> {
            a.setNome(dados.getNome());
            a.setNacionalidade(dados.getNacionalidade());
            return repository.save(a);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public Page<Autor> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable);
    }

}