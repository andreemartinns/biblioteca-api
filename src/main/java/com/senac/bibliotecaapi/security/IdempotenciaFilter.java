package com.senac.bibliotecaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(3)
public class IdempotenciaFilter extends OncePerRequestFilter {

    private static final String CABECALHO = "X-Idempotency-Key";

    // Guarda o estado de uma chave: em processamento ou concluída (com a resposta salva)
    private static class Entrada {
        final String hashCorpo;
        final Instant criadoEm = Instant.now();
        volatile boolean concluida = false;
        volatile int status;
        volatile byte[] corpo;
        volatile String contentType;
        volatile String location;

        Entrada(String hashCorpo) {
            this.hashCorpo = hashCorpo;
        }
    }

    // Request que guarda o corpo em memória, para podermos ler (hash) e ainda repassar ao controller
    private static class RequisicaoComCorpo extends HttpServletRequestWrapper {
        private final byte[] corpo;

        RequisicaoComCorpo(HttpServletRequest req) throws IOException {
            super(req);
            this.corpo = req.getInputStream().readAllBytes();
        }

        byte[] getCorpo() {
            return corpo;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream in = new ByteArrayInputStream(corpo);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public int read() {
                    return in.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            String enc = getCharacterEncoding();
            Charset cs = enc != null ? Charset.forName(enc) : StandardCharsets.UTF_8;
            return new BufferedReader(new InputStreamReader(getInputStream(), cs));
        }
    }

    private final Map<String, Entrada> registros = new ConcurrentHashMap<>();

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
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String chave = requisicao.getHeader(CABECALHO);
        boolean obrigatoria = requisicao.getRequestURI().startsWith("/emprestimos");

        if (chave == null || chave.isBlank()) {
            if (obrigatoria) {
                RespostaErro.escrever(res, 400, "Bad Request",
                        "O cabeçalho X-Idempotency-Key é obrigatório para esta operação");
                return;
            }
            chain.doFilter(requisicao, res);
            return;
        }

        // Lê o corpo uma vez e calcula o hash para comparar com tentativas futuras
        RequisicaoComCorpo req = new RequisicaoComCorpo(requisicao);
        String hash = calcularHash(req.getCorpo());

        // Remove registros concluídos e expirados
        Instant agora = Instant.now();
        registros.values().removeIf(e -> e.concluida && e.criadoEm.plusSeconds(ttlSegundos).isBefore(agora));

        String dono = req.getHeader("X-API-Key") == null ? "" : req.getHeader("X-API-Key");
        String id = req.getRequestURI() + "|" + dono + "|" + chave;

        // putIfAbsent é atômico: só uma requisição "ganha" a chave, mesmo se chegarem ao mesmo tempo
        Entrada nova = new Entrada(hash);
        Entrada existente = registros.putIfAbsent(id, nova);

        if (existente != null) {
            if (!existente.hashCorpo.equals(hash)) {
                RespostaErro.escrever(res, 422, "Unprocessable Entity",
                        "Esta X-Idempotency-Key já foi usada com um corpo de requisição diferente");
                return;
            }
            if (!existente.concluida) {
                RespostaErro.escrever(res, 409, "Conflict",
                        "Já existe uma requisição com esta X-Idempotency-Key em processamento");
                return;
            }
            // Requisição repetida: devolve a resposta guardada, sem processar de novo
            res.setStatus(existente.status);
            if (existente.contentType != null) {
                res.setContentType(existente.contentType);
            }
            if (existente.location != null) {
                res.setHeader("Location", existente.location);
            }
            res.setHeader("Idempotent-Replayed", "true");
            res.getOutputStream().write(existente.corpo);
            return;
        }

        // Primeira vez: processa e guarda a resposta
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(res);
        boolean salvou = false;
        try {
            chain.doFilter(req, wrapper);

            int status = wrapper.getStatus();
            if (status >= 200 && status < 300) {
                nova.status = status;
                nova.corpo = wrapper.getContentAsByteArray();
                nova.contentType = wrapper.getContentType();
                nova.location = wrapper.getHeader("Location");
                nova.concluida = true;
                salvou = true;
            }
        } finally {
            // Se falhou (erro 4xx/5xx ou exceção), libera a chave para o cliente tentar de novo
            if (!salvou) {
                registros.remove(id);
            }
        }
        wrapper.copyBodyToResponse();
    }

    private String calcularHash(byte[] corpo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(corpo));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}