package com.cb.workflow.workflow.controller;

import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.service.WorkflowQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class InboxController {

    private final WorkflowQueryService inboxQueryService;

    public InboxController(WorkflowQueryService inboxQueryService) {
        this.inboxQueryService = inboxQueryService;
    }

    @GetMapping("/inbox")
    public List<InboxItem> inbox(
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return inboxQueryService.myInbox(state, page, size);
    }
}