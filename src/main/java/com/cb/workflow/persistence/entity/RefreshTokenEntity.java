package com.cb.workflow.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RefreshTokenEntity {
    private Long id;
    private Long tenantId;
    private Long userId;
    private String tokenHash;
    private OffsetDateTime expiresAt;
    private OffsetDateTime revokedAt;
    private OffsetDateTime createdAt;
}