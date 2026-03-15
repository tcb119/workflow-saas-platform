package com.cb.workflow.workflow.controller;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.CreateWorkflowRequest;
import com.cb.workflow.workflow.dto.CreateWorkflowResponse;
import com.cb.workflow.workflow.dto.TransitionRequest;
import com.cb.workflow.workflow.dto.TransitionResponse;
import com.cb.workflow.workflow.service.WorkflowCommandService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowCommandService workflowCommandService;

    public WorkflowController(WorkflowCommandService workflowCommandService) {
        this.workflowCommandService = workflowCommandService;
    }

    @PostMapping("/transition")
    public TransitionResponse transition(@AuthenticationPrincipal AuthPrincipal principal,
                                         Authentication authentication,
                                         @RequestBody TransitionRequest req) {
        return workflowCommandService.transition(principal, authentication, req);
    }

    @PostMapping("/create")
    public CreateWorkflowResponse create(@AuthenticationPrincipal AuthPrincipal principal,
                                         @RequestBody @Valid CreateWorkflowRequest req) {
        return workflowCommandService.create(principal, req);
    }
}