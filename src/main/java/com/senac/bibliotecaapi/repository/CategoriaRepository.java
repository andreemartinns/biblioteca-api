package com.senac.bibliotecaapi.repository;

import com.senac.bibliotecaapi.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}