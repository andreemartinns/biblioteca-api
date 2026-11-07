package com.senac.bibliotecaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(3)
public class IdempotenciaFilter extends OncePerRequestFilter {

    private static final String CABECALHO = "X-Idempotency-Key";

    private record Registro(int status, byte[] corpo, String contentType, String location, Instant criadoEm) {
    }

    private final Map<String, Registro> registros = new ConcurrentHashMap<>();

    @Value("${app.idempotency.ttl-seconds:3600}")
    private long ttlSegundos;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getRequestURI();
        return !"POST".equals(req.getMethod())
                || p.startsWith("/api-keys")
                || p.startsWith("/swagger-ui")
                || p.startsWith("/v3/api-docs")
                || p.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String chave = req.getHeader(CABECALHO);
        boolean obrigatoria = req.getRequestURI().startsWith("/emprestimos");

        if (chave == null || chave.isBlank()) {
            if (obrigatoria) {
                RespostaErro.escrever(res, 400, "Bad Request",
                        "O cabeçalho X-Idempotency-Key é obrigatório para esta operação");
                return;
            }
            chain.doFilter(req, res);
            return;
        }

        // Remove registros expirados
        Instant agora = Instant.now();
        registros.values().removeIf(r -> r.criadoEm().plusSeconds(ttlSegundos).isBefore(agora));

        String dono = req.getHeader("X-API-Key") == null ? "" : req.getHeader("X-API-Key");
        String id = req.getRequestURI() + "|" + dono + "|" + chave;

        Registro salvo = registros.get(id);
        if (salvo != null) {
            // Requisição repetida: devolve a resposta guardada, sem processar de novo
            res.setStatus(salvo.status());
            if (salvo.contentType() != null) {
                res.setContentType(salvo.contentType());
            }
            if (salvo.location() != null) {
                res.setHeader("Location", salvo.location());
            }
            res.setHeader("Idempotent-Replayed", "true");
            res.getOutputStream().write(salvo.corpo());
            return;
        }

        // Primeira vez: processa e guarda a resposta
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(res);
        chain.doFilter(req, wrapper);

        int status = wrapper.getStatus();
        if (status >= 200 && status < 300) {
            registros.put(id, new Registro(status, wrapper.getContentAsByteArray(),
                    wrapper.getContentType(), wrapper.getHeader("Location"), Instant.now()));
        }
        wrapper.copyBodyToResponse();
    }
}