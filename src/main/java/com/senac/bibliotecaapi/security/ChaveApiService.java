package com.senac.bibliotecaapi.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ChaveApiService {

    private final ChaveApiRepository repository;

    public ChaveApiService(ChaveApiRepository repository) {
        this.repository = repository;
    }

    public ChaveApi criar(String dono) {
        String valor = UUID.randomUUID().toString().replace("-", "");
        return repository.save(new ChaveApi(valor, dono, NivelAcesso.USUARIO));
    }

    public Page<ChaveApi> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<ChaveApi> revogar(Long id) {
        return repository.findById(id).map(c -> {
            c.setAtiva(false);
            return repository.save(c);
        });
    }
}