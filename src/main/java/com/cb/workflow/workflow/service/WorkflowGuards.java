package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.persistence.entity.WorkflowInstanceEntity;
import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class WorkflowGuards {

    // tenant isolation（租戶隔離）：tenantId 一定要一致（通常 DB query 已帶 tenantId 就夠）
    public void checkTenant(Long tenantIdFromPrincipal, Long tenantIdFromRow) {
        if (!tenantIdFromPrincipal.equals(tenantIdFromRow)) {
            throw new RuntimeException("Tenant mismatch");
        }
    }

    // owner permission（擁有者權限）：resource.ownerId == principal.userId
    public void checkOwnerForSubmit(AuthPrincipal p,
                           WorkflowInstanceEntity inst,
                           String action) {

        if ("SUBMIT".equalsIgnoreCase(action)) {
            if (!p.getUserId().equals(inst.getOwnerUserId())) {
                throw new RuntimeException("Not owner");
            }
        }
    }

    // state validation（狀態驗證）：transition 是否存在
    public void checkTransitionExists(WorkflowTransitionEntity t) {
        if (t == null) throw new RuntimeException("Invalid transition");
    }

    public void checkInstanceExists(WorkflowInstanceEntity inst) {
        if (inst == null) {
            throw new RuntimeException("Workflow instance not found");
        }
    }

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
}