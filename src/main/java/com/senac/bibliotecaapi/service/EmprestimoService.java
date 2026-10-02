package com.senac.bibliotecaapi.service;

import com.senac.bibliotecaapi.entity.Emprestimo;
import com.senac.bibliotecaapi.entity.StatusEmprestimo;
import com.senac.bibliotecaapi.repository.EmprestimoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;

    public EmprestimoService(EmprestimoRepository repository) {
        this.repository = repository;
    }

    public Page<Emprestimo> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Emprestimo> buscarPorStatus(StatusEmprestimo status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Emprestimo salvar(Emprestimo emprestimo) {
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        return repository.save(emprestimo);
    }

    public Optional<Emprestimo> atualizar(Long id, Emprestimo dados) {
        return repository.findById(id).map(e -> {
            e.setUsuario(dados.getUsuario());
            e.setLivro(dados.getLivro());
            e.setDataPrevista(dados.getDataPrevista());
            e.setDataDevolucao(dados.getDataDevolucao());
            if (dados.getStatus() != null) {
                e.setStatus(dados.getStatus());
            }
            return repository.save(e);
        });
    }

    // Registra a devolução: preenche a data e muda o status
    public Optional<Emprestimo> devolver(Long id) {
        return repository.findById(id).map(e -> {
            e.setDataDevolucao(LocalDate.now());
            e.setStatus(StatusEmprestimo.DEVOLVIDO);
            return repository.save(e);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}