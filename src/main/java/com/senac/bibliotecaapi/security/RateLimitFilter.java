package com.senac.bibliotecaapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static class Janela {
        long inicio;
        int contagem;

        Janela(long inicio) {
            this.inicio = inicio;
        }
    }

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.max-requests:30}")
    private int maximo;

    @Value("${app.rate-limit.window-seconds:60}")
    private int janelaSegundos;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String p = req.getRequestURI();
        return p.startsWith("/swagger-ui") || p.startsWith("/v3/api-docs") || p.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String chaveApi = req.getHeader("X-API-Key");
        String cliente = (chaveApi != null && !chaveApi.isBlank())
                ? "key:" + chaveApi
                : "ip:" + req.getRemoteAddr();

        long agora = System.currentTimeMillis();
        long duracao = janelaSegundos * 1000L;
        Janela janela = janelas.computeIfAbsent(cliente, k -> new Janela(agora));

        boolean bloqueado;
        int restante;
        long retryApos;

        synchronized (janela) {
            if (agora - janela.inicio >= duracao) {
                janela.inicio = agora;
                janela.contagem = 0;
            }
            janela.contagem++;
            bloqueado = janela.contagem > maximo;
            restante = Math.max(0, maximo - janela.contagem);
            retryApos = Math.max(1, (janela.inicio + duracao - agora + 999) / 1000);
        }

        res.setHeader("X-RateLimit-Limit", String.valueOf(maximo));
        res.setHeader("X-RateLimit-Remaining", String.valueOf(restante));

        if (bloqueado) {
            res.setHeader("Retry-After", String.valueOf(retryApos));
            RespostaErro.escrever(res, 429, "Too Many Requests",
                    "Limite de " + maximo + " requisições por " + janelaSegundos
                            + " segundos excedido. Tente novamente em " + retryApos + " segundos");
            return;
        }

        chain.doFilter(req, res);
    }
}