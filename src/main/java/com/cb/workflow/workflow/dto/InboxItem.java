package com.cb.workflow.workflow.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class InboxItem {
    private Long instanceId;
    private String state;
    private Long ownerId;
    private Long assigneeUserId;
    private String assigneeRoleCode;
    private OffsetDateTime updatedAt;
}