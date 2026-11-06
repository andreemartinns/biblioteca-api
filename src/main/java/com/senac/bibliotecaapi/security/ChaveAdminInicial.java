package com.senac.bibliotecaapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChaveAdminInicial implements CommandLineRunner {

    private final ChaveApiRepository repository;

    @Value("${app.admin-key}")
    private String chaveAdmin;

    public ChaveAdminInicial(ChaveApiRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(new ChaveApi(chaveAdmin, "Administrador", NivelAcesso.ADMIN));
    }
}