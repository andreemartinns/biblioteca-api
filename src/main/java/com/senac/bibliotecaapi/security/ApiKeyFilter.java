package com.senac.bibliotecaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(2)
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ChaveApiRepository repository;

    public ApiKeyFilter(ChaveApiRepository repository) {
        this.repository = repository;
    }

    // Retorna o nível mínimo exigido, ou null se a rota é pública
    private NivelAcesso nivelExigido(String metodo, String path) {
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console") || path.startsWith("/error")) {
            return null;
        }
        if (path.startsWith("/api-keys")) {
            return "POST".equals(metodo) ? null : NivelAcesso.ADMIN;
        }
        return switch (metodo) {
            case "POST", "PUT", "PATCH" -> NivelAcesso.USUARIO;
            case "DELETE" -> NivelAcesso.ADMIN;
            default -> null; // GET, HEAD, OPTIONS
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        NivelAcesso exigido = nivelExigido(req.getMethod(), req.getRequestURI());

        if (exigido == null) {
            chain.doFilter(req, res);
            return;
        }

        String chave = req.getHeader("X-API-Key");
        if (chave == null || chave.isBlank()) {
            RespostaErro.escrever(res, 401, "Unauthorized", "Cabeçalho X-API-Key ausente");
            return;
        }

        Optional<ChaveApi> encontrada = repository.findByChaveAndAtivaTrue(chave);
        if (encontrada.isEmpty()) {
            RespostaErro.escrever(res, 401, "Unauthorized", "API key inválida ou revogada");
            return;
        }

        if (exigido == NivelAcesso.ADMIN && encontrada.get().getNivel() != NivelAcesso.ADMIN) {
            RespostaErro.escrever(res, 403, "Forbidden", "Esta operação exige uma chave de nível ADMIN");
            return;
        }

        chain.doFilter(req, res);
    }
}