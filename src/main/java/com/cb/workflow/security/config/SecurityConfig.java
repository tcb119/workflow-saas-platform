package com.cb.workflow.security.config;

import com.cb.workflow.rbac.service.RbacService;
import com.cb.workflow.security.jwt.JwtAuthenticationFilter;
import com.cb.workflow.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtService jwtService,
                                           RbacService rbacService) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, rbacService);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}