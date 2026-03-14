package com.cb.workflow.workflow.controller;

import com.cb.workflow.workflow.dto.AdminTransitionRuleItem;
import com.cb.workflow.workflow.dto.CreateTransitionRuleRequest;
import com.cb.workflow.workflow.dto.CreateTransitionRuleResponse;
import com.cb.workflow.workflow.service.AdminWorkflowConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/workflow")
public class AdminWorkflowConfigController {

    private final AdminWorkflowConfigService adminWorkflowConfigService;

    public AdminWorkflowConfigController(AdminWorkflowConfigService adminWorkflowConfigService) {
        this.adminWorkflowConfigService = adminWorkflowConfigService;
    }

    @GetMapping("/transitions")
    public List<AdminTransitionRuleItem> listRules() {
        return adminWorkflowConfigService.listRules();
    }

    @PostMapping("/transitions")
    public CreateTransitionRuleResponse createRule(@RequestBody CreateTransitionRuleRequest req) {
        return adminWorkflowConfigService.createRule(req);
    }

    @PatchMapping("/transitions/{id}/active")
    public void updateActive(@PathVariable Long id,
                             @RequestParam Boolean isActive) {
        adminWorkflowConfigService.updateActive(id, isActive);
    }
}