package com.cb.workflow.workflow.controller;

import com.cb.workflow.workflow.dto.ApprovalLogItem;
import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.dto.WorkflowDetailResponse;
import com.cb.workflow.workflow.service.WorkflowQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowQueryController {

    private final WorkflowQueryService workflowQueryService;

    public WorkflowQueryController(WorkflowQueryService workflowQueryService) {
        this.workflowQueryService = workflowQueryService;
    }

    @GetMapping("/inbox")
    public List<InboxItem> inbox(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(required = false) String state) {
        return workflowQueryService.myInbox(state, page, size);
    }

    @GetMapping("/my-requests")
    public List<InboxItem> myRequests(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return workflowQueryService.myRequests(page, size);
    }

    @GetMapping("/history")
    public List<ApprovalLogItem> history(@RequestParam Long instanceId) {
        return workflowQueryService.history(instanceId);
    }

    @GetMapping("/detail")
    public WorkflowDetailResponse detail(@RequestParam Long instanceId) {
        return workflowQueryService.detail(instanceId);
    }
}