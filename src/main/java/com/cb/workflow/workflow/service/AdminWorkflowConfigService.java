package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.AdminTransitionRuleItem;
import com.cb.workflow.workflow.dto.CreateTransitionRuleRequest;
import com.cb.workflow.workflow.dto.CreateTransitionRuleResponse;
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

        WorkflowTransitionEntity entity = new WorkflowTransitionEntity();
        entity.setTenantId(principal.getTenantId());
        entity.setFromState(req.getFromState());
        entity.setAction(req.getAction());
        entity.setToState(req.getToState());
        entity.setRequiredRole(req.getRequiredRole());
        entity.setNextAssigneeUserId(req.getNextAssigneeUserId());
        entity.setNextAssigneeRoleCode(req.getNextAssigneeRoleCode());
        entity.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);

        transitionMapper.insertRule(entity);

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
}