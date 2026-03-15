package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.AdminTransitionRuleItem;
import com.cb.workflow.workflow.dto.CreateTransitionRuleRequest;
import com.cb.workflow.workflow.dto.CreateTransitionRuleResponse;
import com.cb.workflow.workflow.dto.UpdateTransitionRuleRequest;
import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import com.cb.workflow.workflow.persistence.mapper.WorkflowTransitionMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminWorkflowConfigService {

    private final WorkflowTransitionMapper transitionMapper;

    public AdminWorkflowConfigService(WorkflowTransitionMapper transitionMapper) {
        this.transitionMapper = transitionMapper;
    }

    public List<AdminTransitionRuleItem> listRules() {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return transitionMapper.findAllRules(principal.getTenantId());
    }

    @Transactional
    public CreateTransitionRuleResponse createRule(CreateTransitionRuleRequest req) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        NormalizedRule n = normalizeRule(
                req.getFromState(),
                req.getAction(),
                req.getToState(),
                req.getRequiredRole(),
                req.getNextAssigneeRoleCode(),
                req.getIsActive()
        );

        String fromState = n.fromState;
        String action = n.action;
        String toState = n.toState;
        String requiredRole = n.requiredRole;
        String nextAssigneeRoleCode = n.nextAssigneeRoleCode;
        Boolean isActive = n.isActive;

        validateRuleFields(fromState, action, toState);

        boolean exists = transitionMapper.existsByTenantAndFromStateAndAction(
                principal.getTenantId(),
                fromState,
                action
        );
        if (exists) {
            throw new RuntimeException("Duplicate transition rule: fromState + action already exists");
        }

        WorkflowTransitionEntity entity = new WorkflowTransitionEntity();
        entity.setTenantId(principal.getTenantId());
        entity.setFromState(fromState);
        entity.setAction(action);
        entity.setToState(toState);
        entity.setRequiredRole(requiredRole);
        entity.setNextAssigneeUserId(req.getNextAssigneeUserId());
        entity.setNextAssigneeRoleCode(nextAssigneeRoleCode);
        entity.setIsActive(isActive);

        transitionMapper.insertRule(entity);

        return toResponse(entity);
    }

    @Transactional
    public CreateTransitionRuleResponse updateRule(Long id, UpdateTransitionRuleRequest req) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        WorkflowTransitionEntity existing = transitionMapper.findById(principal.getTenantId(), id);
        if (existing == null) {
            throw new RuntimeException("Transition rule not found");
        }

        NormalizedRule n = normalizeRule(
                req.getFromState(),
                req.getAction(),
                req.getToState(),
                req.getRequiredRole(),
                req.getNextAssigneeRoleCode(),
                req.getIsActive()
        );

        String fromState = n.fromState;
        String action = n.action;
        String toState = n.toState;
        String requiredRole = n.requiredRole;
        String nextAssigneeRoleCode = n.nextAssigneeRoleCode;
        Boolean isActive = n.isActive;

        validateRuleFields(fromState, action, toState);

        boolean exists = transitionMapper.existsByTenantAndFromStateAndActionExcludingId(
                principal.getTenantId(),
                fromState,
                action,
                id
        );
        if (exists) {
            throw new RuntimeException("Duplicate transition rule: fromState + action already exists");
        }

        existing.setFromState(fromState);
        existing.setAction(action);
        existing.setToState(toState);
        existing.setRequiredRole(requiredRole);
        existing.setNextAssigneeUserId(req.getNextAssigneeUserId());
        existing.setNextAssigneeRoleCode(nextAssigneeRoleCode);
        existing.setIsActive(isActive);

        int updated = transitionMapper.updateRule(existing);
        if (updated == 0) {
            throw new RuntimeException("Failed to update transition rule");
        }

        return toResponse(existing);
    }

    @Transactional
    public void updateActive(Long id, Boolean isActive) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        int updated = transitionMapper.updateActive(principal.getTenantId(), id, isActive);
        if (updated == 0) {
            throw new RuntimeException("Transition rule not found");
        }
    }

    private CreateTransitionRuleResponse toResponse(WorkflowTransitionEntity entity) {
        return CreateTransitionRuleResponse.builder()
                .id(entity.getId())
                .fromState(entity.getFromState())
                .action(entity.getAction())
                .toState(entity.getToState())
                .requiredRole(entity.getRequiredRole())
                .nextAssigneeUserId(entity.getNextAssigneeUserId())
                .nextAssigneeRoleCode(entity.getNextAssigneeRoleCode())
                .isActive(entity.getIsActive())
                .build();
    }

    private static class NormalizedRule {
        String fromState;
        String action;
        String toState;
        String requiredRole;
        String nextAssigneeRoleCode;
        Boolean isActive;

        NormalizedRule(String fromState,
                       String action,
                       String toState,
                       String requiredRole,
                       String nextAssigneeRoleCode,
                       Boolean isActive) {
            this.fromState = fromState;
            this.action = action;
            this.toState = toState;
            this.requiredRole = requiredRole;
            this.nextAssigneeRoleCode = nextAssigneeRoleCode;
            this.isActive = isActive;
        }
    }

    private NormalizedRule normalizeRule(String fromState,
                                         String action,
                                         String toState,
                                         String requiredRole,
                                         String nextAssigneeRoleCode,
                                         Boolean isActive) {

        String nFromState = normalizeRequired(fromState, "fromState");
        String nAction = normalizeRequired(action, "action");
        String nToState = normalizeRequired(toState, "toState");
        String nRequiredRole = normalizeOptional(requiredRole);
        String nNextRole = normalizeOptional(nextAssigneeRoleCode);
        Boolean nIsActive = isActive != null ? isActive : true;

        return new NormalizedRule(
                nFromState,
                nAction,
                nToState,
                nRequiredRole,
                nNextRole,
                nIsActive
        );
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(fieldName + " must not be blank");
        }
        return value.trim().toUpperCase();
    }

    private String normalizeOptional(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private void validateRuleFields(String fromState, String action, String toState) {
        if (fromState.equals(toState)) {
            throw new RuntimeException("fromState and toState must not be the same");
        }

        if (action.length() > 50) {
            throw new RuntimeException("action is too long");
        }

        if (fromState.length() > 50 || toState.length() > 50) {
            throw new RuntimeException("state value is too long");
        }
    }
}