package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.persistence.entity.WorkflowInstanceEntity;
import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WorkflowGuards {

    public void validateTransition(AuthPrincipal principal,
                                   Authentication authentication,
                                   WorkflowInstanceEntity inst,
                                   WorkflowTransitionEntity transition,
                                   String action,
                                   String requestId) {

        checkInstanceExists(inst);
        checkTransitionExists(transition);
        checkOwnerForSubmit(principal, inst, action);
        checkDuplicateRequest(inst, requestId);

        // SUBMIT 不看 role，只看 owner
        if (!"SUBMIT".equalsIgnoreCase(action)) {
            checkRole(authentication, transition.getRequiredRole());
        }
    }

    // tenant isolation（租戶隔離）：tenantId 一定要一致（通常 DB query 已帶 tenantId 就夠）
    /**
    public void checkTenant(Long tenantIdFromPrincipal, Long tenantIdFromRow) {
        if (!tenantIdFromPrincipal.equals(tenantIdFromRow)) {
            throw new RuntimeException("Tenant mismatch");
        }
    }**/

    public void checkInstanceExists(WorkflowInstanceEntity inst) {
        if (inst == null) {
            throw new RuntimeException("Workflow instance not found");
        }
    }

    // owner permission（擁有者權限）：resource.ownerId == principal.userId => 誰才能更改
    public void checkOwnerForSubmit(AuthPrincipal p,
                           WorkflowInstanceEntity inst,
                           String action) {

        if ("SUBMIT".equalsIgnoreCase(action)) {
            if (!p.getUserId().equals(inst.getOwnerUserId())) {
                throw new RuntimeException("Only owner can submit");
            }
        }
    }

    // state validation（狀態驗證）：transition 是否存在
    public void checkTransitionExists(WorkflowTransitionEntity t) {
        if (t == null) throw new RuntimeException("Invalid transition");
    }

    // 例如 APPROVE 必須是 ADMIN
    public void checkRole(Authentication authentication, String requiredRole) {
        if (requiredRole == null || requiredRole.isBlank()) {
            return;
        }

        String expected = requiredRole.startsWith("ROLE_")
                ? requiredRole
                : "ROLE_" + requiredRole;

        boolean ok = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> expected.equals(a.getAuthority()));

        if (!ok) {
            throw new RuntimeException("Forbidden: role not allowed");
        }
    }

    public void checkDuplicateRequest(WorkflowInstanceEntity inst, String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new RuntimeException("requestId is required");
        }

        if (requestId.equals(inst.getLastTransitionRequestId())) {
            throw new RuntimeException("Duplicate transition request");
        }
    }
}