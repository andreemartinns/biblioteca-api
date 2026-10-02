package com.senac.bibliotecaapi.repository;

import com.senac.bibliotecaapi.entity.PerfilUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {

    Page<PerfilUsuario> findByTelefoneContaining(String telefone, Pageable pageable);
}