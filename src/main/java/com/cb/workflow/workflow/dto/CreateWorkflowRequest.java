package com.cb.workflow.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWorkflowRequest {

    @NotBlank
    private String title;
}