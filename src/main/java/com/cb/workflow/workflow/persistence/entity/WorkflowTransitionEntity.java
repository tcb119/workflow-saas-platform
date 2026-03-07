package com.cb.workflow.workflow.persistence.entity;

import lombok.Data;

@Data
public class WorkflowTransitionEntity {
    private Long id;
    private Long tenantId;

    private String fromState;
    private String action;
    private String toState;

    private String requiredRole; // RBAC（角色授權）可選：例如 "ADMIN"
    private Long nextAssigneeUserId;   // optional
    private String nextAssigneeRoleCode; // optional
}