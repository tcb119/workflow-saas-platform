package com.cb.workflow.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateWorkflowResponse {
    private Long instanceId;
    private String state;
    private Long ownerId;
    private String title;
}