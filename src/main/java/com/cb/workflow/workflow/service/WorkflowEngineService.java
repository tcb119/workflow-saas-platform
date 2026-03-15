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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {

    private final WorkflowInstanceMapper instanceMapper;
    private final WorkflowTransitionMapper transitionMapper;
    private final WorkflowApprovalLogMapper approvalLogMapper;
    private final WorkflowGuards guards;


    // transition（狀態遷移）主流程：load → guard → find transition → optimistic update → log
    @Transactional
    public TransitionResponse transition(AuthPrincipal p, TransitionRequest req) {
        Long tenantId = p.getTenantId();

        WorkflowInstanceEntity inst = instanceMapper.findByTenantAndId(tenantId, req.getInstanceId());
        if (inst == null) {
            throw new RuntimeException("Instance not found");
        }

        // A 路線：先 owner guard（之後可擴充為 approver inbox）
        guards.checkOwnerForSubmit(p, inst, req.getAction());

        WorkflowTransitionEntity transition = transitionMapper.findTransition(tenantId, inst.getState(), req.getAction());
        guards.checkTransitionExists(transition);

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
            // optimistic locking conflict（樂觀鎖衝突）或 duplicate request（重複請求）
            throw new RuntimeException("Concurrent modification or duplicate request");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // approval log（簽核紀錄 / audit evidence）
        WorkflowApprovalLogEntity log = WorkflowApprovalLogEntity.builder()
                .tenantId(tenantId)
                .instanceId(inst.getId())
                .actorUserId(p.getUserId())
                .actorUserName(p.getUsername())   // 新增
                .actorRole(
                        authentication.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority()
                )                                 // 新增
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
                .build();
    }
}