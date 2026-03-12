package com.cb.workflow.workflow.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ApprovalLogItem {
    private Long id;
    private Long instanceId;
    private Long actorUserId;
    private String actorUserName;
    private String actorRole;
    private String action;
    private String fromState;
    private String toState;
    private String comment;
    private String requestId;
    private OffsetDateTime createdAt;
}