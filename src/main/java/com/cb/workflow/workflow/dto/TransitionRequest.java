package com.cb.workflow.workflow.dto;

import lombok.Data;

@Data
public class TransitionRequest {
    private Long instanceId;
    private String action;     // e.g. "SUBMIT", "APPROVE", "REJECT"
    private String requestId;  // idempotency key（冪等鍵）
    private String comment;    // optional
}