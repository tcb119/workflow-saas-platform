package com.cb.workflow.workflow.dto;

import lombok.Data;

@Data
public class AdminTransitionRuleItem {
    private Long id;
    private String fromState;
    private String action;
    private String toState;
    private String requiredRole;
    private Long nextAssigneeUserId;
    private String nextAssigneeRoleCode;
    private Boolean isActive;
}