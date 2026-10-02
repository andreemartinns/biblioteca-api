package com.senac.bibliotecaapi.service;

import com.senac.bibliotecaapi.entity.PerfilUsuario;
import com.senac.bibliotecaapi.repository.PerfilUsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilUsuarioService {

    private final PerfilUsuarioRepository repository;

    public PerfilUsuarioService(PerfilUsuarioRepository repository) {
        this.repository = repository;
    }

    public Page<PerfilUsuario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<PerfilUsuario> buscarPorTelefone(String telefone, Pageable pageable) {
        return repository.findByTelefoneContaining(telefone, pageable);
    }

    public Optional<PerfilUsuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public PerfilUsuario salvar(PerfilUsuario perfil) {
        return repository.save(perfil);
    }

    public Optional<PerfilUsuario> atualizar(Long id, PerfilUsuario dados) {
        return repository.findById(id).map(p -> {
            p.setTelefone(dados.getTelefone());
            p.setEndereco(dados.getEndereco());
            p.setDataNascimento(dados.getDataNascimento());
            return repository.save(p);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}