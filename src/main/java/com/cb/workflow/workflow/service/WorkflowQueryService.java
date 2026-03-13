package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.ApprovalLogItem;
import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.dto.WorkflowDetailResponse;
import com.cb.workflow.workflow.persistence.mapper.WorkflowApprovalLogMapper;
import com.cb.workflow.workflow.persistence.mapper.WorkflowInboxMapper;
import com.cb.workflow.workflow.persistence.mapper.WorkflowInstanceMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkflowQueryService {

    private final WorkflowInboxMapper inboxMapper;
    private final WorkflowApprovalLogMapper approvalLogMapper;
    private final WorkflowInstanceMapper instanceMapper;

    public WorkflowQueryService(WorkflowInboxMapper inboxMapper,
                                WorkflowApprovalLogMapper approvalLogMapper,
                                WorkflowInstanceMapper instanceMapper) {
        this.inboxMapper = inboxMapper;
        this.approvalLogMapper = approvalLogMapper;
        this.instanceMapper = instanceMapper;
    }

    public WorkflowDetailResponse detail(Long instanceId) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return instanceMapper.findDetail(principal.getTenantId(), instanceId);
    }

    public List<InboxItem> myInbox(String state, int page, int size) {

        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Long tenantId = principal.getTenantId();
        Long userId = principal.getUserId();

        List<String> roleCodes = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(a -> a.getAuthority())         // e.g. ROLE_ADMIN
                .map(s -> s.startsWith("ROLE_") ? s.substring("ROLE_".length()) : s) // -> ADMIN
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100); // size 1~100
        int offset = safePage * safeSize;

        return inboxMapper.findInbox(
                tenantId,
                userId,
                roleCodes,
                state,
                safeSize,
                offset
        );
    }

    public List<ApprovalLogItem> history(Long instanceId) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return approvalLogMapper.findHistory(principal.getTenantId(), instanceId);
    }
}