package com.cb.workflow.auth.service;

import com.cb.workflow.auth.dto.LoginRequest;
import com.cb.workflow.auth.dto.TokenResponse;
import com.cb.workflow.persistence.entity.UserEntity;
import com.cb.workflow.persistence.mapper.RefreshTokenMapper;
import com.cb.workflow.persistence.mapper.UserMapper;
import com.cb.workflow.security.jwt.JwtService;
import com.cb.workflow.security.jwt.RefreshTokenSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenSupport refreshTokenSupport;
    private final long refreshTtlDays;

    public AuthService(
            UserMapper userMapper,
            RefreshTokenMapper refreshTokenMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenSupport refreshTokenSupport,
            @Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays
    ) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenSupport = refreshTokenSupport;
        this.refreshTtlDays = refreshTtlDays;
    }

    public TokenResponse login(LoginRequest req) {
        Long tenantId = req.getTenantId() == null ? 1L : req.getTenantId();

        UserEntity user = userMapper.findByTenantAndEmail(tenantId, req.getEmail());
        if (user == null) throw new RuntimeException("Invalid credentials");

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String access = jwtService.generateAccessToken(tenantId, user.getId(), user.getEmail());

        String rawRefresh = refreshTokenSupport.newRawToken();
        String hash = refreshTokenSupport.sha256Base64Url(rawRefresh);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(refreshTtlDays);

        refreshTokenMapper.insert(tenantId, user.getId(), hash, expiresAt);

        return TokenResponse.of(access, rawRefresh);
    }

    public TokenResponse refresh(Long tenantId, String rawRefreshToken) {
        String hash = refreshTokenSupport.sha256Base64Url(rawRefreshToken);
        Long userId = refreshTokenMapper.findActiveUserId(tenantId, hash);
        if (userId == null) throw new RuntimeException("Invalid refresh token");

        UserEntity user = userMapper.findByTenantAndId(tenantId, userId);
        if (user == null) throw new RuntimeException("User not found");

        String access = jwtService.generateAccessToken(tenantId, user.getId(), user.getEmail());
        return TokenResponse.of(access, rawRefreshToken);
    }

    public void logout(Long tenantId, String rawRefreshToken) {
        String hash = refreshTokenSupport.sha256Base64Url(rawRefreshToken);
        refreshTokenMapper.revoke(tenantId, hash);
    }
}