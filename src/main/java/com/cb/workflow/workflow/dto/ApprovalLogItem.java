package com.cb.workflow.workflow.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ApprovalLogItem {
    private Long id;
    private Long instanceId;
    private Long actorUserId;
    private String action;
    private String fromState;
    private String toState;
    private String comment;
    private OffsetDateTime createdAt;
}