package com.cb.workflow.workflow.controller;

import com.cb.workflow.workflow.dto.ApprovalLogItem;
import com.cb.workflow.workflow.service.WorkflowQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowHistoryController {

    private final WorkflowQueryService workflowQueryService;

    public WorkflowHistoryController(WorkflowQueryService workflowQueryService) {
        this.workflowQueryService = workflowQueryService;
    }

    @GetMapping("/history/{instanceId}")
    public List<ApprovalLogItem> history(@PathVariable Long instanceId) {
        return workflowQueryService.history(instanceId);
    }
}