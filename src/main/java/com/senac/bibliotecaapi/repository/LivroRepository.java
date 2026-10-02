package com.senac.bibliotecaapi.repository;

import com.senac.bibliotecaapi.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Consulta personalizada: busca livros pelo título (ignora maiúsculas)
    Page<Livro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}