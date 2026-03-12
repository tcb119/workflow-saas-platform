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

        // 1) load instance
        WorkflowInstanceEntity inst = instanceMapper.findByTenantAndId(
                tenantId,
                req.getInstanceId()
        );

        // 2) load transition rule
        WorkflowTransitionEntity transition = null;
        if (inst != null) {
            transition = transitionMapper.findTransition(
                    tenantId,
                    inst.getState(),
                    req.getAction()
            );
        }

        // 3) validate guard pipeline
        guards.validateTransition(
                principal,
                authentication,
                inst,
                transition,
                req.getAction(),
                req.getRequestId()
        );

        // 4) optimistic locking + duplicate protection
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

        // 5) write approval log
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