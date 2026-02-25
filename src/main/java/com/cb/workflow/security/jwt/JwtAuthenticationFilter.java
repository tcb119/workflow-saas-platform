package com.cb.workflow.security.jwt;

import com.cb.workflow.security.principal.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.cb.workflow.rbac.service.RbacService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RbacService rbacService;

    public JwtAuthenticationFilter(JwtService jwtService, RbacService rbacService) {
        this.jwtService = jwtService;
        this.rbacService = rbacService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 沒帶 Authorization → 交給後面規則決定要不要擋
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring("Bearer ".length()).trim();

        try {
            AuthPrincipal principal = jwtService.parseAndVerify(token);

            // RBAC lookup per request（每次請求查角色）
            var roleCodes = rbacService.getRoleCodes(principal.getTenantId(), principal.getUserId());

            var authorities = roleCodes.stream()
                    .map(code -> code.startsWith("ROLE_") ? code : "ROLE_" + code)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // Authentication（認證物件）
            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken(principal, null, List.of());
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            // set to SecurityContext（放進安全上下文）
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (JwtService.JwtAuthException e) {
            // 企業常用：直接 401 + 清楚訊息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {"error":"UNAUTHORIZED","message":"%s"}
                    """.formatted(e.getMessage()));
        }
    }
}