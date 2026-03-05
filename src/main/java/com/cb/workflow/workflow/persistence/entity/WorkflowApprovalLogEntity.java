package com.cb.workflow.workflow.persistence.entity;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class WorkflowApprovalLogEntity {
    private Long id;

    private Long tenantId;
    private Long instanceId;

    private Long actorUserId;

    private String actorUserName; // optional (snapshot)
    private String actorRole;     // optional (snapshot)

    private String action;
    private String fromState;
    private String toState;

    private String comment;

    private String requestId;     // ✅ 一定要有（對應 request_id）

    private OffsetDateTime createdAt;
}