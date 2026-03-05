package com.cb.workflow.workflow.controller;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.service.WorkflowQueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inbox")
public class InboxController {

    private final WorkflowQueryService queryService;

    public InboxController(WorkflowQueryService queryService) {
        this.queryService = queryService;
    }

    // GET /api/inbox/me?limit=20
    @GetMapping("/me")
    public List<InboxItem> myInbox(@AuthenticationPrincipal AuthPrincipal p,
                                   @RequestParam(defaultValue = "20") int limit) {
        return queryService.myInbox(p, limit);
    }
}