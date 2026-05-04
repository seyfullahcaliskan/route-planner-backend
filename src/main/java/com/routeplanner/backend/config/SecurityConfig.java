//package com.routeplanner.backend.Config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
///**
// * FAZ 1 — geçici güvenlik yapılandırması.
// * Tüm endpoint'ler public; auth yoktur.
// *
// * FAZ 2'de yapılacaklar:
// *   - JWT filtresi eklenecek (JwtAuthenticationFilter)
// *   - /api/v1/auth/** açık kalacak
// *   - Diğer tüm endpoint'ler authenticated() olacak
// *   - Plan limiti kontrolü @PreAuthorize + custom SecurityExpression ile eklenecek
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Swagger UI
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        // FAZ 1: tüm API endpoint'leri açık
//                        // FAZ 2: aşağıdaki satırı kaldır, JWT filtresi ekle
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }
//}
