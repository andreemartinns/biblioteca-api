package com.senac.bibliotecaapi.security;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public final class RespostaErro {

    private RespostaErro() {
    }

    public static void escrever(HttpServletResponse res, int status, String nome, String mensagem) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        String json = "{\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status + ","
                + "\"erro\":\"" + nome + "\","
                + "\"mensagem\":\"" + mensagem + "\"}";
        res.getWriter().write(json);
    }
}