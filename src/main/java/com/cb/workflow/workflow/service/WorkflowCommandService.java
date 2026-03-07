package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.TransitionRequest;
import com.cb.workflow.workflow.dto.TransitionResponse;
import com.cb.workflow.workflow.persistence.entity.WorkflowApprovalLogEntity;
import com.cb.workflow.workflow.persistence.entity.WorkflowInstanceEntity;
import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import com.cb.workflow.workflow.persistence.mapper.WorkflowApprovalLogMapper;
import com.cb.workflow.workflow.persistence.mapper.WorkflowInstanceMapper;
import com.cb.workflow.workflow.persistence.mapper.WorkflowTransitionMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowCommandService {

    private final WorkflowInstanceMapper instanceMapper;
    private final WorkflowTransitionMapper transitionMapper;
    private final WorkflowApprovalLogMapper approvalLogMapper;
    private final WorkflowGuards guards;

    public WorkflowCommandService(
            WorkflowInstanceMapper instanceMapper,
            WorkflowTransitionMapper transitionMapper,
            WorkflowApprovalLogMapper approvalLogMapper,
            WorkflowGuards guards
    ) {
        this.instanceMapper = instanceMapper;
        this.transitionMapper = transitionMapper;
        this.approvalLogMapper = approvalLogMapper;
        this.guards = guards;
    }

    @Transactional
    public TransitionResponse transition(AuthPrincipal principal,
                                         Authentication authentication,
                                         TransitionRequest req) {

        Long tenantId = principal.getTenantId();

        WorkflowInstanceEntity inst = instanceMapper.findByTenantAndId(
                tenantId,
                req.getInstanceId()
        );
        guards.checkInstanceExists(inst);

        guards.checkOwnerForSubmit(principal, inst, req.getAction());

        WorkflowTransitionEntity transition = transitionMapper.findTransition(
                tenantId,
                inst.getState(),
                req.getAction()
        );
        guards.checkTransitionExists(transition);

        guards.checkRole(authentication, transition.getRequiredRole());

        int updated = instanceMapper.updateStateWithOptimisticLock(
                tenantId,
                inst.getId(),
                inst.getVersion(),
                transition.getToState(),
                transition.getNextAssigneeUserId(),
                transition.getNextAssigneeRoleCode(),
                req.getRequestId()
        );

        if (updated == 0) {
            throw new RuntimeException("Transition conflict or duplicate request");
        }

        WorkflowApprovalLogEntity log = WorkflowApprovalLogEntity.builder()
                .tenantId(tenantId)
                .instanceId(inst.getId())
                .actorUserId(principal.getUserId())
                .actorUserName(String.valueOf(principal.getUserId()))
                .actorRole(
                        authentication.getAuthorities()
                                .stream()
                                .findFirst()
                                .map(a -> a.getAuthority())
                                .orElse("ROLE_UNKNOWN")
                )
                .action(req.getAction())
                .fromState(inst.getState())
                .toState(transition.getToState())
                .comment(req.getComment())
                .requestId(req.getRequestId())
                .build();

        approvalLogMapper.insert(log);

        return TransitionResponse.builder()
                .instanceId(inst.getId())
                .fromState(inst.getState())
                .toState(transition.getToState())
                .version(inst.getVersion() + 1)
                .assigneeUserId(transition.getNextAssigneeUserId())
                .assigneeRoleCode(transition.getNextAssigneeRoleCode())
                .build();
    }
}