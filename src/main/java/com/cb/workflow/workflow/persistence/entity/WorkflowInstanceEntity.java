package com.cb.workflow.workflow.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class WorkflowInstanceEntity {
    private Long id;
    private Long tenantId;
    private Long ownerUserId;

    private String state;
    private Long version;              // optimistic locking（樂觀鎖）版本號

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}