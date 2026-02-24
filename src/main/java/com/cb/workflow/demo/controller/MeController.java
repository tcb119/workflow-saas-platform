package com.cb.workflow.demo.controller;

import com.cb.workflow.security.principal.AuthPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/api/me")
    public Map<String, Object> me() {
        AuthPrincipal p = (AuthPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("tenantId", p.getTenantId());
        res.put("userId", p.getUserId());
        res.put("email", p.getEmail());
        return res;
    }
}