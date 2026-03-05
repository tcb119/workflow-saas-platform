package com.cb.workflow.workflow.service;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.persistence.mapper.WorkflowInboxMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkflowQueryService {

    private final WorkflowInboxMapper inboxMapper;

    public WorkflowQueryService(WorkflowInboxMapper inboxMapper) {
        this.inboxMapper = inboxMapper;
    }

    public List<InboxItem> myInbox(AuthPrincipal p, int limit) {
        return inboxMapper.listOwnerInbox(p.getTenantId(), p.getUserId(), limit);
    }
}