package com.cb.workflow.workflow.controller;

import com.cb.workflow.security.principal.AuthPrincipal;
import com.cb.workflow.workflow.dto.TransitionRequest;
import com.cb.workflow.workflow.dto.TransitionResponse;
import com.cb.workflow.workflow.service.WorkflowEngineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowEngineService engineService;

    public WorkflowController(WorkflowEngineService engineService) {
        this.engineService = engineService;
    }

    // POST /api/workflows/transition
    @PostMapping("/transition")
    public TransitionResponse transition(@AuthenticationPrincipal AuthPrincipal p,
                                         @RequestBody TransitionRequest req) {
        return engineService.transition(p, req);
    }
}