package com.alpha.service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 9/10/2025
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())     // <- WAJIB supaya pakai bean corsConfigurationSource
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <- preflight
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Origin
        cfg.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://192.168.1.2:3000",
                "http://103.10.58.123:3000",
                "http://103.10.58.123"
        ));

        // Methods
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        // Headers (case-insensitive, tapi eksplisit)
        cfg.setAllowedHeaders(List.of(
                "Content-Type","Authorization","X-Requested-With",
                "X-Idempotency-Key","x-idempotency-key","Accept","Origin","Cache-Control","Pragma"
        ));

        // Header yang boleh diekspos ke FE (opsional)
        cfg.setExposedHeaders(List.of("X-Idempotency-Key","Location"));

        // FE pakai withCredentials: true => server wajib allowCredentials + origin spesifik (bukan *)
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
