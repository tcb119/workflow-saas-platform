package com.cb.workflow.workflow.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class InboxItem {
    private Long instanceId;
    private String state;
    private String title;               // demo欄位：之後可換成你的 domain 欄位
    private OffsetDateTime updatedAt;
}