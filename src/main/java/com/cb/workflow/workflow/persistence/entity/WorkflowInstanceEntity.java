package com.cb.workflow.workflow.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class WorkflowInstanceEntity {
    private Long id;
    private Long tenantId;
    private Long ownerUserId;

    private Long assigneeUserId;
    private String assigneeRoleCode;

    private String state;
    private Long version;
    private String title;

    private String lastTransitionRequestId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}