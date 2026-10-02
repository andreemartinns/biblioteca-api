package com.senac.bibliotecaapi.repository;

import com.senac.bibliotecaapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta personalizada: busca usuários pelo nome
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}