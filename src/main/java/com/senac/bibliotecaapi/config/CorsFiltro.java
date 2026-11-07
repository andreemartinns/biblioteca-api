package com.senac.bibliotecaapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFiltro extends CorsFilter {

    public CorsFiltro(@Value("${app.cors.allowed-origins}") String origens) {
        super(configuracao(origens));
    }

    private static UrlBasedCorsConfigurationSource configuracao(String origens) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origens.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Accept",
                "X-API-Key", "X-Idempotency-Key", "X-API-Version"));
        config.setExposedHeaders(List.of("Retry-After", "X-RateLimit-Limit",
                "X-RateLimit-Remaining", "Idempotent-Replayed", "Location"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}