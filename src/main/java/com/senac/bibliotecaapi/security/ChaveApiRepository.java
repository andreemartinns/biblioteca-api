package com.senac.bibliotecaapi.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChaveApiRepository extends JpaRepository<ChaveApi, Long> {

    Optional<ChaveApi> findByChaveAndAtivaTrue(String chave);
}