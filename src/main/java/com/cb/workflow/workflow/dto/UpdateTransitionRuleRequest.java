package com.cb.workflow.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTransitionRuleRequest {

    @NotBlank
    private String fromState;

    @NotBlank
    private String action;

    @NotBlank
    private String toState;

    private String requiredRole;
    private Long nextAssigneeUserId;
    private String nextAssigneeRoleCode;
    private Boolean isActive;
}