package com.cb.workflow.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTransitionRuleResponse {
    private Long id;
    private String fromState;
    private String action;
    private String toState;
    private String requiredRole;
    private Long nextAssigneeUserId;
    private String nextAssigneeRoleCode;
    private Boolean isActive;
}