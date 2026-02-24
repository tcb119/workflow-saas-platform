package com.cb.workflow.security.config;

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

@Configuration
public class SecurityConfig {

    /**
     * PasswordEncoder（密碼雜湊器）
     * - 用在 login 時比對 passwordHash
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain（安全過濾鏈）
     * - 定義哪些路徑要驗證、哪些放行
     * - 插入 JwtAuthenticationFilter（JWT 認證過濾器）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService);

        http
                // CSRF（跨站請求偽造防護）：
                // 後端純 API + token 通常關掉（因為不是用 cookie session）
                .csrf(AbstractHttpConfigurer::disable)

                // Session（伺服器端 session）：
                // JWT 是 stateless（無狀態），通常不靠 session
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                ))

                // Authorization（授權規則：哪些 endpoint 要登入、哪些放行）
                .authorizeHttpRequests(auth -> auth
                        // 公開端點（permitAll/全部放行）
                        .requestMatchers("/api/health", "/actuator/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // 其他 API 一律要已登入（authenticated/已認證）
                        .requestMatchers("/api/**").authenticated()

                        // 其餘都先放行（你也可以改成 authenticated）
                        .anyRequest().permitAll()
                )

                // 插入 JwtAuthenticationFilter：
                // 放在 UsernamePasswordAuthenticationFilter 之前
                // 意義：先做 Bearer token 認證，再進入後面流程
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 其他預設（可先不加）
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}