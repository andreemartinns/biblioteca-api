package com.senac.bibliotecaapi.repository;

import com.senac.bibliotecaapi.entity.Emprestimo;
import com.senac.bibliotecaapi.entity.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Consulta personalizada: filtra empréstimos pelo status
    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable pageable);
}