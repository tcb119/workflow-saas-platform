package com.cb.workflow.workflow.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TransitionResponse {
    private Long instanceId;
    private String fromState;
    private String toState;
    private Long version;      // optimistic locking（樂觀鎖）用
}