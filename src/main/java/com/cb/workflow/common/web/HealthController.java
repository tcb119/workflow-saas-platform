package com.cb.workflow.common.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.application.name:workflow-saas-platform}")
    private String appName;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "UP");
        res.put("time", OffsetDateTime.now().toString()); // ISO-8601
        res.put("app", appName);
        return res;
    }
}