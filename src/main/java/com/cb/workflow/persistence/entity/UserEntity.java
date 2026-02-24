package com.cb.workflow.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserEntity {
    private Long id;
    private Long tenantId;
    private String email;
    private String passwordHash;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}