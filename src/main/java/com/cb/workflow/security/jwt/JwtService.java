package com.cb.workflow.security.jwt;

import com.cb.workflow.security.principal.AuthPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long accessTtlSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-ttl-minutes}") long accessTtlMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTtlSeconds = accessTtlMinutes * 60;
    }

    // token issuance（令牌簽發）
    public String generateAccessToken(Long tenantId, Long userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claims(Map.of(
                        "tenantId", tenantId,
                        "email", email
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .signWith(key)          // signature（簽章）
                .compact();             // serialization（序列化）
    }

    // token verification（驗證）+ claims parsing（載荷解析）
    public AuthPrincipal parseAndVerify(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)    // verification key（驗證金鑰）
                    .requireIssuer(issuer) // issuer check（簽發者檢查）
                    .build()
                    .parseSignedClaims(token);

            Claims c = jws.getPayload();

            Long userId = Long.valueOf(c.getSubject());
            Long tenantId = toLong(c.get("tenantId"));
            String email = c.get("email", String.class);

            if (tenantId == null || email == null) {
                throw new JwtAuthException("Missing required claims");
            }

            return new AuthPrincipal(tenantId, userId, email);

        } catch (SignatureException e) {
            throw new JwtAuthException("Invalid signature", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new JwtAuthException("Token expired", e);
        } catch (Exception e) {
            throw new JwtAuthException("Invalid token", e);
        }
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) return Long.valueOf(s);
        return null;
    }

    // custom exception（自訂例外）
    public static class JwtAuthException extends RuntimeException {
        public JwtAuthException(String msg) { super(msg); }
        public JwtAuthException(String msg, Throwable cause) { super(msg, cause); }
    }
}